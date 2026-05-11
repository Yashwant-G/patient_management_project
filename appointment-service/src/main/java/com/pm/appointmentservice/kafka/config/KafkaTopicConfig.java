//package com.pm.appointmentservice.kafka.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Bean
//    public NewTopic doctorTopic() {
//        return TopicBuilder.name("doctor-events")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic paymentTopic() {
//        return TopicBuilder.name("payment-events")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//}
