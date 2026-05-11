package com.pm.patientservice.kafka.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaTopicConfig {

    @Bean
    public NewTopic patientCreatedTopic() {
        return TopicBuilder.name("patient_topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic patientUpdatedTopic() {
        return TopicBuilder.name("patient_updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic billingTopic() {
        return TopicBuilder.name("billing_topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
