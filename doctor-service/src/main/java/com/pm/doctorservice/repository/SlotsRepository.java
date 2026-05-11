package com.pm.doctorservice.repository;

import com.pm.doctorservice.entity.Slots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface SlotsRepository extends JpaRepository<Slots, UUID> {
    Boolean existsByDoctor_DoctorIdAndSlotDateAndStartTime(UUID doctorId, LocalDate slotDate, LocalTime startTime);
}
