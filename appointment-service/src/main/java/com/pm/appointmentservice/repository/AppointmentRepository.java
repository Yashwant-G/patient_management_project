package com.pm.appointmentservice.repository;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.entity.Appointment;
import com.pm.appointmentservice.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<AppointmentStatus> Active_Booking_Status = List.of(
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.PAYMENT_PENDING,
            AppointmentStatus.PENDING
    );

    @Query("""
            SELECT new com.pm.appointmentservice.dto.AppointmentResponseDto(
            a.appointmentId,
            a.patientId,
            cp.fullName,
            a.doctorName,
            a.appointmentDate,
            a.startTime,
            a.endTime,
            a.reason,
            a.version
            )
            FROM Appointment a
            JOIN a.cachedPatient cp
            WHERE a.appointmentDate BETWEEN :from AND :to
            """)
    List<AppointmentResponseDto> findByAppointmentDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    Boolean existsByRequestId(UUID requestId);

    Optional<AppointmentStatus> findByPatientIdAndAppointmentDateAndStartTimeAndAppointmentStatusIn(
            UUID patientId, LocalDate appointmentDate, LocalTime startTime, List<AppointmentStatus> appointmentStatuses
    );

    default Optional<AppointmentStatus> findExistingAppointment(
            UUID patientId, LocalDate appointmentDate, LocalTime startTime
    ) {
        return findByPatientIdAndAppointmentDateAndStartTimeAndAppointmentStatusIn(
                patientId,
                appointmentDate,
                startTime,
                Active_Booking_Status
        );
    }
}
