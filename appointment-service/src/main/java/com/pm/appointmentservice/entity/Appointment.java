package com.pm.appointmentservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID appointmentId;

    @NotNull(message = "Patient id is required")
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", insertable = false, updatable = false)
    private CachedPatient cachedPatient;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @NotNull
    @Column(name = "doctor_name")
    private String doctorName;

    @NotNull(message = "Start Time is required")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End Time is required")
    @Column(nullable = false)
    @Future(message = "End Time must be in Future")
    private LocalDateTime endTime;

    @NotNull(message = "Reason is required")
    @Column(nullable = false)
    @Size(max = 255, message = "Reason should be less than 255 Characters")
    private String reason;

    @Version
    @Column(nullable = false)
    private long version; //keeps a version, which gets incremented by +1 on every update

    public CachedPatient getCachedPatient() {
        return cachedPatient;
    }

    public void setCachedPatient(CachedPatient cachedPatient) {
        this.cachedPatient = cachedPatient;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }


    public Appointment() {
    }

    public Appointment(UUID appointmentId, UUID patientId, LocalDateTime startTime, LocalDateTime endTime, String reason, long version) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.version = version;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}

