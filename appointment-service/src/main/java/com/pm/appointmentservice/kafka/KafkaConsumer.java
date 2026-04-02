package com.pm.appointmentservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.appointmentservice.entity.CachedPatient;
import com.pm.appointmentservice.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

import java.time.Instant;
import java.util.UUID;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    AppointmentService appointmentService;

    public KafkaConsumer(AppointmentService appointmentService){
        this.appointmentService=appointmentService;
    }

    @KafkaListener(topics = {"patient_topic","patient_updated"}, groupId = "appointment-service")
    public void consumeEvent(byte[] event){
        try {
            PatientEvent patientEvent=PatientEvent.parseFrom(event);
            log.info("Consumer event in appointment-service: {}",patientEvent.toString());

            CachedPatient cachedPatient=new CachedPatient();
            cachedPatient.setEmail(patientEvent.getEmail());
            cachedPatient.setFullName(patientEvent.getName());
            cachedPatient.setId(UUID.fromString(patientEvent.getPatientId()));
            cachedPatient.setUpdatedAt(Instant.now());

            appointmentService.syncCachedPatientTable(cachedPatient);

        } catch (InvalidProtocolBufferException e) {
            log.error("Error Deserializing event: {}",e.getMessage());
        } catch(Exception e){
            log.error("Error Consuming event: {}",e.getMessage());
        }
    }
}
