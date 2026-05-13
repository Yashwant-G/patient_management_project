package com.pm.appointmentservice.service;

import com.pm.appointmentservice.client.DoctorServiceClient;
import com.pm.appointmentservice.dto.AppointmentRequestDto;
import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.dto.DoctorClientDto;
import com.pm.appointmentservice.entity.Appointment;
import com.pm.appointmentservice.entity.CachedPatient;
import com.pm.appointmentservice.entity.enums.AppointmentStatus;
import com.pm.appointmentservice.exception.DoctorNotFoundException;
import com.pm.appointmentservice.exception.PatientNotFoundException;
import com.pm.appointmentservice.grpc.AiServiceGrpcClient;
import com.pm.appointmentservice.repository.AppointmentRepository;
import com.pm.appointmentservice.repository.CachedPatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;
    private final AiServiceGrpcClient aiServiceGrpcClient;
    private final DoctorServiceClient doctorServiceClient;
    private final SagaOrchestratorService sagaOrchestratorService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            CachedPatientRepository cachedPatientRepository,
            AiServiceGrpcClient aiServiceGrpcClient,
            DoctorServiceClient doctorServiceClient, SagaOrchestratorService sagaOrchestratorService) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
        this.aiServiceGrpcClient = aiServiceGrpcClient;
        this.doctorServiceClient = doctorServiceClient;
        this.sagaOrchestratorService = sagaOrchestratorService;
    }

    public List<AppointmentResponseDto> getAppointmentByDateRange(LocalDate from, LocalDate to) {
        log.info("Fetching appointments between {} and {}", from, to);
        return appointmentRepository.findByAppointmentDateBetween(from, to);
    }

    public void syncCachedPatientTable(CachedPatient cachedPatient) {
        log.info("Syncing cached patient: id={}, name={}", cachedPatient.getId(), cachedPatient.getFullName());
        cachedPatientRepository.save(cachedPatient);
        log.info("Cached patient saved/updated in DB: id={}", cachedPatient.getId());
    }

    public AppointmentResponseDto AiAddAppointment(String plainText, UUID patientId) {
        log.info("AiAddAppointment called for patientId={}", patientId);

        CachedPatient patient = cachedPatientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));
        log.info("Found patient: name={}", patient.getFullName());

        // Enrich the prompt with patient context only; doctor name is extracted by AI from the text
        plainText = plainText
                + " Patient id is " + patientId
                + " and Patient name is " + patient.getFullName();

        log.info("Sending text to AI service for parsing");
        AppointmentResponseDto response = aiServiceGrpcClient.parseAppointment(plainText);
        log.info("AI service returned: patientName={}, doctorName={}", response.getPatientName(), response.getDoctorName());

        // Search doctor via Doctor Service by word-matching against the name extracted by AI
        log.info("Calling Doctor Service to search for doctor by name: {}", response.getDoctorName());
        DoctorClientDto doctor = doctorServiceClient.searchDoctorByName(response.getDoctorName());

        if (doctor == null) {
            log.error("Doctor not found with name: {}", response.getDoctorName());
            throw new DoctorNotFoundException("Doctor not found with name: " + response.getDoctorName());
        }
        log.info("Matched doctor from Doctor Service: id={}, name={}", doctor.getDoctorId(), doctor.getFullName());

//        response.setDoctorId(doctor.getDoctorId());
        response.setDoctorName(doctor.getFullName());

        Appointment appointment = new Appointment();
        appointment.setRequestId(UUID.randomUUID());
        appointment.setPatientId(response.getPatientId());
        appointment.setStartTime(response.getStartTime());
        appointment.setEndTime(response.getEndTime());
        appointment.setReason(response.getReason());
        appointment.setDoctorId(doctor.getDoctorId());
        appointment.setDoctorName(doctor.getFullName());

        // Set remaining attributes
        appointment.setAppointmentDate(response.getAppointmentDate());
        appointment.setSagaId(UUID.randomUUID());
        appointment.setSlotId(null); // Will be set during saga flow
        appointment.setAmount(java.math.BigDecimal.ZERO); // Default amount
        appointment.setPaymentMethod(null); // Will be set during booking
        appointment.setTxnId(null); // Will be set after payment
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);

        log.info("Saving AI appointment to database - patientId={}, doctorId={}", response.getPatientId(), doctor.getDoctorId());
        UUID appointmentId = appointmentRepository.save(appointment).getAppointmentId();
        log.info("Appointment saved with id={}", appointmentId);

        response.setId(appointmentId);
        response.setStatus("Ai-Appointment Successfully Booked");

        return response;
    }


    public AppointmentResponseDto bookAppointment(AppointmentRequestDto appointmentRequestDto){
        log.info("Booking flow initiated - requestId={}, patientId={}, doctorId={}",
                appointmentRequestDto.getRequestId(),
                appointmentRequestDto.getPatientId(),
                appointmentRequestDto.getDoctorId());

        CachedPatient patient = cachedPatientRepository.findById(appointmentRequestDto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + appointmentRequestDto.getPatientId()));
        log.debug("Patient validated: name={}", patient.getFullName());

        if(appointmentRepository.existsByRequestId(appointmentRequestDto.getRequestId())){
            log.warn("Duplicate booking attempt detected for requestId={}", appointmentRequestDto.getRequestId());
            throw new RuntimeException("Appointment already exists for this request id: "+appointmentRequestDto.getRequestId());
        }

        Optional<AppointmentStatus> appointmentExist=appointmentRepository.findExistingAppointment(
                appointmentRequestDto.getPatientId(),
                appointmentRequestDto.getAppointment_date(),
                appointmentRequestDto.getStartTime());
        if(appointmentExist.isPresent()){
            log.warn("Appointment already exists with Status: ={}", appointmentExist.get());
            throw new RuntimeException("Appointment already exists with status: "+appointmentExist.get());
        }

        Appointment appointment=new Appointment();
        appointment.setRequestId(appointmentRequestDto.getRequestId());
        appointment.setPatientId(patient.getId());
        appointment.setCachedPatient(patient);
        appointment.setDoctorId(appointmentRequestDto.getDoctorId());
        appointment.setAppointmentDate(appointmentRequestDto.getAppointment_date());
        appointment.setStartTime(appointmentRequestDto.getStartTime());
        appointment.setEndTime(appointmentRequestDto.getEndTime());
        appointment.setReason(appointmentRequestDto.getReason());
        appointment.setPaymentMethod(appointmentRequestDto.getPaymentMethod());

        log.info("Initiating saga orchestration for requestId={}", appointmentRequestDto.getRequestId());
        return sagaOrchestratorService.startSaga(appointment);
    }
}
