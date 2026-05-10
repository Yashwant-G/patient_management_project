package com.pm.appointmentservice.dto;

import java.util.UUID;

/**
 * DTO for receiving doctor information from Doctor Service
 */
public class DoctorClientDto {

    private UUID doctorId;
    private String fullName;
    private String specialization;
    private String email;

    public DoctorClientDto() {
    }

    public DoctorClientDto(UUID doctorId, String fullName) {
        this.doctorId = doctorId;
        this.fullName = fullName;
    }

    public DoctorClientDto(UUID doctorId, String fullName, String specialization, String email) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.email = email;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

