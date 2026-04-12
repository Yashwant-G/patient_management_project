package com.pm.appointmentservice.service;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.entity.Appointment;
import com.pm.appointmentservice.entity.CachedPatient;
import com.pm.appointmentservice.exception.PatientNotFoundException;
import com.pm.appointmentservice.grpc.AiServiceGrpcClient;
import com.pm.appointmentservice.repository.AppointmentRepository;
import com.pm.appointmentservice.repository.CachedPatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;
    private final AiServiceGrpcClient aiServiceGrpcClient;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            CachedPatientRepository cachedPatientRepository,
            AiServiceGrpcClient aiServiceGrpcClient) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
        this.aiServiceGrpcClient=aiServiceGrpcClient;
    }

    public List<AppointmentResponseDto> getAppointmentByDateRange(LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findByStartTimeBetween(from, to);
    }

    public void syncCachedPatientTable(CachedPatient cachedPatient) {
        cachedPatientRepository.save(cachedPatient);
    }

    public AppointmentResponseDto AiAddAppointment(String plainText) {

        AppointmentResponseDto response=aiServiceGrpcClient.parseAppointment(plainText);

        CachedPatient patient = cachedPatientRepository.findById(response.getPatientId())
                .orElseThrow(()->new PatientNotFoundException("Patient not found with id: "+response.getPatientId()));

        response.setPatientName(patient.getFullName());

        Appointment appointment = new Appointment();

        appointment.setPatientId(response.getPatientId());
        appointment.setStartTime(response.getStartTime());
        appointment.setEndTime(response.getEndTime());
        appointment.setReason(response.getReason());

        UUID appointmentId = appointmentRepository.save(appointment).getAppointmentId();

        response.setId(appointmentId);

        return response;

    }

}
