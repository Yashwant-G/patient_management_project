package com.pm.doctorservice.service;

import com.pm.doctorservice.dto.DoctorResponseDto;
import com.pm.doctorservice.dto.DoctorSearchDto;
import com.pm.doctorservice.entity.Doctor;
import com.pm.doctorservice.exception.DoctorNotFoundException;
import com.pm.doctorservice.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Search for a doctor by name using full-text search
     */
    public DoctorSearchDto searchDoctorByName(String name) {
        log.info("Searching for doctor with name: {}", name);
        Doctor doctor = doctorRepository.searchByName(name)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with name: " + name));
        log.info("Found doctor: id={}, name={}", doctor.getDoctorId(), doctor.getFullName());
        return new DoctorSearchDto(doctor.getDoctorId(), doctor.getFullName());
    }

    /**
     * Get full doctor details by ID
     */
    public DoctorResponseDto getDoctorById(UUID doctorId) {
        log.info("Fetching doctor details for id={}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        log.info("Found doctor: id={}, name={}", doctor.getDoctorId(), doctor.getFullName());
        return mapToResponseDto(doctor);
    }

    /**
     * Get minimal doctor information (for appointment service)
     */
    public DoctorSearchDto getDoctorMinimalById(UUID doctorId) {
        log.info("Fetching minimal doctor info for id={}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        return new DoctorSearchDto(doctor.getDoctorId(), doctor.getFullName());
    }

    /**
     * Check if doctor exists
     */
    public boolean exists(UUID doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    /**
     * Validate doctor exists and return minimal info
     */
    public DoctorSearchDto validateAndGetDoctor(UUID doctorId) {
        log.info("Validating doctor exists: id={}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        return new DoctorSearchDto(doctor.getDoctorId(), doctor.getFullName());
    }

    private DoctorResponseDto mapToResponseDto(Doctor doctor) {
        return new DoctorResponseDto(
                doctor.getDoctorId(),
                doctor.getFullName(),
                doctor.getSpecialization(),
                doctor.getEmail()
        );
    }
}

