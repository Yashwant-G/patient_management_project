package com.pm.patientservice.Service;

import com.pm.patientservice.DTO.PagedPatientDTO;
import com.pm.patientservice.DTO.PatientDTO;
import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.Entity.Patient;
import com.pm.patientservice.Mapper.PatientMapper;
import com.pm.patientservice.Repository.PatientRepository;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.kafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final kafkaProducer kafkaProducer;

    private static final Set<String> ALLOWED_FIELDS =
            Set.of("name", "email");

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, kafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }


//    Key:   patients::1
//    Value: { JSON of API response }
    @Cacheable(
            key = "#page + '-' + #size + '-' + #sort + '-' + #sortField",
            value = "patients",
            condition = "#searchValue == ''"
    )
    public PagedPatientDTO getPatients(int page, int size, String sort, String sortField, String searchValue) {

        log.info("[REDIS]: cache miss-fetching from db");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        //Page is 0 based indexing
        if (!ALLOWED_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("Invalid search field: " + sortField);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending());
        Page<Patient> patientPage;

        if (searchValue == null || searchValue.isEmpty()) {
            patientPage = patientRepository.findAll(pageable);
        } else {
            //for dynamic searching
            Specification<Patient> spec = (root, query, cb) ->
                    cb.like(root.get(sortField), "%" + searchValue + "%");
            // can use query for query.distinct()/groupBy()/orderBy(),etc

            patientPage = patientRepository.findAll(spec, pageable);
        }


        List<PatientDTO> patientDTOS = patientPage.getContent().stream().map(PatientMapper::toDto).toList();
        return new PagedPatientDTO(
                patientDTOS,
                patientPage.getNumber() + 1,
                patientPage.getSize(),
                patientPage.getTotalPages(),
                (int) patientPage.getTotalElements()
        );

//        List<Patient> patients = patientRepository.findAll();
//        return patients.stream().map(PatientMapper::toDto).toList();
    }

    public PatientDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + patientRequestDTO.getEmail() + " already exists");
        }
        Patient patient = patientRepository.save(PatientMapper.toEntity(patientRequestDTO));
        log.info("Patient created with id: {}", patient.getId());

        billingServiceGrpcClient.createBillingAccount(patient);

        kafkaProducer.sendEvent(patient, "patient_topic", "PATIENT_CREATED");

        return PatientMapper.toDto(patient);
    }

    public PatientDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Patient with Email " + patientRequestDTO.getEmail() + " already exists");
        }
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        kafkaProducer.sendEvent(updatedPatient, "patient_updated", "PATIENT_UPDATED");

        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient with id " + id + " not found");
        }
        patientRepository.deleteById(id);
    }
}
