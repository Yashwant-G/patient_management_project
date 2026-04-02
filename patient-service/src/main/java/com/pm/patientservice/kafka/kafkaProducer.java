package com.pm.patientservice.kafka;

import com.pm.patientservice.Entity.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

@Service
public class kafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(kafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public kafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient, String topic, String evenType) {
        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEventType(evenType)
                .setEmail(patient.getEmail())
                .build();
        try {
            kafkaTemplate.send(topic, patientEvent.toByteArray());
            log.info("{} Event sent successfully to topic [{}]: {}", evenType, topic, patientEvent);
        } catch (Exception e) {
            log.error("Error sending the {} Event to topic [{}]: {}", evenType, topic, patientEvent);
            log.error("Failed with error: {}", e.getMessage());
        }
    }
}
