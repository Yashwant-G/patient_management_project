package com.pm.appointmentservice.repository;

import com.pm.appointmentservice.entity.CachedPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CachedPatientRepository extends JpaRepository<CachedPatient, UUID> {
}
