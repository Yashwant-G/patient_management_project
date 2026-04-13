package com.pm.appointmentservice.repository;

import com.pm.appointmentservice.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    /**
     * Searches for a doctor by matching all words in the input against the full_name (case-insensitive).
     * Examples:
     *   "Dr Sarah Patel"  → matches "Dr. Sarah Patel"
     *   "dr sarah"        → matches "Dr. Sarah Patel"
     *   "dr patel"        → matches "Dr. Sarah Patel"
     */
    default Optional<Doctor> searchByName(String name) {
        if (name == null || name.isBlank()) return Optional.empty();
        String[] words = name.trim().toLowerCase().split("\\s+");
        return findAll().stream()
                .filter(d -> {
                    String lower = d.getFullName().toLowerCase();
                    return Arrays.stream(words).allMatch(lower::contains);
                })
                .findFirst();
    }
}
