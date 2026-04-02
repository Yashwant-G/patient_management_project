package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient_topic", groupId = "analytics-service")
    public void consumeEvent(byte[] event){
        try{
            PatientEvent patientEvent=PatientEvent.parseFrom(event);
            //Business logic for analytics can be added here

            log.info("Consumed event: {}", patientEvent.toString());
        } catch (InvalidProtocolBufferException e) {
            log.error("Event consume failed with error: {}",e.getMessage());
        }
    }
}
