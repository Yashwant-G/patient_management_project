package com.pm.doctorservice.service;

import com.pm.doctorservice.dto.DoctorResponseDto;
import com.pm.doctorservice.dto.DoctorSearchDto;
import com.pm.doctorservice.entity.Doctor;
import com.pm.doctorservice.exception.DoctorNotFoundException;
import com.pm.doctorservice.repository.DoctorRepository;
import doctor.DoctorEventRequest;
import doctor.DoctorEventResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final SlotsService slotsService;

    public DoctorService(DoctorRepository doctorRepository, SlotsService slotsService) {
        this.doctorRepository = doctorRepository;
        this.slotsService = slotsService;
    }

    /**
     * Search for a doctor by name using full-text search
     */
    public DoctorSearchDto searchDoctorByName(String name) {
        log.info("Searching for doctor with name: {}", name);
        Doctor doctor = doctorRepository.searchByName(name)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with name: " + name));
        log.info("Found doctor: id={}, name={}", doctor.getDoctorId(), doctor.getFullName());
        return new DoctorSearchDto(doctor.getDoctorId(), doctor.getFullName());
    }

    /**
     * Get full doctor details by ID
     */
    public DoctorResponseDto getDoctorById(UUID doctorId) {
        log.info("Fetching doctor details for id={}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        log.info("Found doctor: id={}, name={}", doctor.getDoctorId(), doctor.getFullName());
        return mapToResponseDto(doctor);
    }

    /**
     * Get minimal doctor information (for appointment service)
     */
    public DoctorSearchDto getDoctorMinimalById(UUID doctorId) {
        log.info("Fetching minimal doctor info for id={}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        return new DoctorSearchDto(doctor.getDoctorId(), doctor.getFullName());
    }
    public Boolean exists(UUID doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    public DoctorEventResponse validateDoctorEvent(DoctorEventRequest request){
        //validate doctor
        Doctor doctor = doctorRepository.findById(UUID.fromString(request.getDoctorId()))
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        //Validate Slots
        UUID slotId=slotsService.validateAppointmentSlot(request,doctor);

        return DoctorEventResponse.newBuilder()
                .setSlotId(slotId.toString())
                .setFees(doctor.getFees().toString())
                .build();
    }

    private DoctorResponseDto mapToResponseDto(Doctor doctor) {
        return new DoctorResponseDto(
                doctor.getDoctorId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getEmail()
        );
    }
}

