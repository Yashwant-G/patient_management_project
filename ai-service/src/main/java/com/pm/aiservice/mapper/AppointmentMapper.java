package com.pm.aiservice.mapper;

import ai.ParseAppointmentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppointmentMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ParseAppointmentResponse mapToProto(String json) {

        try {
            JsonNode node = objectMapper.readTree(json);

            return ParseAppointmentResponse.newBuilder()
                    .setPatientId(node.get("patient_id").asText(""))
                    .setStartTime(node.get("start_time").asText(""))
                    .setEndTime(node.get("end_time").asText(""))
                    .setReason(node.get("reason").asText(""))
                    .setPatientName(node.get("patient_name").asText(""))
                    .setDoctorName(node.get("doctor_name").asText(""))
                    .setAppointmentDate(node.get("appointment_date").asText(""))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
