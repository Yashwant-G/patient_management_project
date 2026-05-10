package com.pm.appointmentservice.repository;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("""
            SELECT new com.pm.appointmentservice.dto.AppointmentResponseDto(
            a.appointmentId,
            a.patientId,
            cp.fullName,
            a.doctorId,
            a.doctorName,
            a.startTime,
            a.endTime,
            a.reason,
            a.version
            )
            FROM Appointment a
            JOIN a.cachedPatient cp
            WHERE a.startTime BETWEEN :from AND :to
            """)
    List<AppointmentResponseDto> findByStartTimeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
