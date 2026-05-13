package com.pm.appointmentservice.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AppointmentResponseDto {

    private UUID id;
    private UUID patientId;
    private String patientName;
    private String doctorName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String reason;
    private Long version; // ➡️ Added version for optimistic locking


    public AppointmentResponseDto() {
    }

    public AppointmentResponseDto(UUID id, UUID patientId, String patientName,
                                  String doctorName, LocalDate appointmentDate,
                                  LocalTime startTime, LocalTime endTime, String reason, Long version) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.version = version;
    }

    public AppointmentResponseDto(UUID id, UUID patientId, String patientName, String doctorName, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime, String status, String reason, Long version) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.reason = reason;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
