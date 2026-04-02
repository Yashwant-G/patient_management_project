package com.pm.appointmentservice.service;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.entity.CachedPatient;
import com.pm.appointmentservice.repository.AppointmentRepository;
import com.pm.appointmentservice.repository.CachedPatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, CachedPatientRepository cachedPatientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
    }

    public List<AppointmentResponseDto> getAppointmentByDateRange(LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findByStartTimeBetween(from, to);
    }

    public void syncCachedPatientTable(CachedPatient cachedPatient) {
        cachedPatientRepository.save(cachedPatient);
    }

}
