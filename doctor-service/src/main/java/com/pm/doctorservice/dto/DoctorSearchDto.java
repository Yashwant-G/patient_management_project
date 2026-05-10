package com.pm.doctorservice.dto;

import java.util.UUID;

public class DoctorSearchDto {

    private UUID doctorId;
    private String fullName;

    public DoctorSearchDto() {
    }

    public DoctorSearchDto(UUID doctorId, String fullName) {
        this.doctorId = doctorId;
        this.fullName = fullName;
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
}

