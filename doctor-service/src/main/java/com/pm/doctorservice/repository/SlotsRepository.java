package com.pm.doctorservice.repository;

import com.pm.doctorservice.entity.Slots;
import com.pm.doctorservice.entity.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface SlotsRepository extends JpaRepository<Slots, UUID> {
    Boolean existsByDoctor_DoctorIdAndSlotDateAndStartTime(UUID doctorId, LocalDate slotDate, LocalTime startTime);

    Long deleteAllByExpiresAtBeforeAndStatus(LocalDateTime now, SlotStatus status);
}
