package com.pm.appointmentservice.service;

import com.pm.appointmentservice.client.DoctorServiceClient;
import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.dto.DoctorClientDto;
import com.pm.appointmentservice.entity.Appointment;
import com.pm.appointmentservice.entity.CachedPatient;
import com.pm.appointmentservice.exception.DoctorNotFoundException;
import com.pm.appointmentservice.exception.PatientNotFoundException;
import com.pm.appointmentservice.grpc.AiServiceGrpcClient;
import com.pm.appointmentservice.repository.AppointmentRepository;
import com.pm.appointmentservice.repository.CachedPatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;
    private final AiServiceGrpcClient aiServiceGrpcClient;
    private final DoctorServiceClient doctorServiceClient;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            CachedPatientRepository cachedPatientRepository,
            AiServiceGrpcClient aiServiceGrpcClient,
            DoctorServiceClient doctorServiceClient) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
        this.aiServiceGrpcClient = aiServiceGrpcClient;
        this.doctorServiceClient = doctorServiceClient;
    }

    public List<AppointmentResponseDto> getAppointmentByDateRange(LocalDateTime from, LocalDateTime to) {
        log.info("Fetching appointments between {} and {}", from, to);
        return appointmentRepository.findByStartTimeBetween(from, to);
    }

    public void syncCachedPatientTable(CachedPatient cachedPatient) {
        log.info("Syncing cached patient: id={}, name={}", cachedPatient.getId(), cachedPatient.getFullName());
        cachedPatientRepository.save(cachedPatient);
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
            throw new DoctorNotFoundException("Doctor not found with name: " + response.getDoctorName());
        }
        log.info("Matched doctor from Doctor Service: id={}, name={}", doctor.getDoctorId(), doctor.getFullName());

        response.setDoctorId(doctor.getDoctorId());
        response.setDoctorName(doctor.getFullName());

        Appointment appointment = new Appointment();
        appointment.setPatientId(response.getPatientId());
        appointment.setStartTime(response.getStartTime());
        appointment.setEndTime(response.getEndTime());
        appointment.setReason(response.getReason());
        appointment.setDoctorId(doctor.getDoctorId());
        appointment.setDoctorName(doctor.getFullName());

        UUID appointmentId = appointmentRepository.save(appointment).getAppointmentId();
        log.info("Appointment saved with id={}", appointmentId);

        response.setId(appointmentId);

        return response;
    }
}
