package com.pm.patientservice.Mapper;

import com.pm.patientservice.DTO.PatientDTO;
import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.Entity.Patient;

import java.time.LocalDate;

public class PatientMapper {
    public static PatientDTO toDto(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId().toString());
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setDateOfBirth(patient.getDateOfBirth().toString());
        return dto;
    }

    public static Patient toEntity(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return patient;
    }
}
