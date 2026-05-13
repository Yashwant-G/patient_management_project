package com.pm.aiservice.service;


import ai.ParseAppointmentResponse;
import com.pm.aiservice.config.ChatConfig;
import com.pm.aiservice.mapper.AppointmentMapper;
import com.pm.aiservice.prompt.AppointmentPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AiParsingService {
    private static final Logger log = LoggerFactory.getLogger(AiParsingService.class);
    private final ChatConfig chatConfig;
    private final AppointmentPrompt appointmentPrompt;

    public AiParsingService(ChatConfig chatConfig, AppointmentPrompt appointmentPrompt) {
        this.chatConfig = chatConfig;
        this.appointmentPrompt = appointmentPrompt;
    }

    public ParseAppointmentResponse parse(String text){
        log.info("Starting appointment text parsing");
        try {
            String prompt = appointmentPrompt.buildPrompt(text);
            log.info("Prompt built successfully for text parsing");
            log.debug("Prompt: {}", prompt);

            log.info("Calling Gemini AI model to process appointment text");
            String aiResponse = chatConfig.call(prompt);
            log.info("Received AI response from Gemini model");
            log.debug("Raw AI Response: {}", aiResponse);

            log.info("Mapping AI response to protobuf format");
            ParseAppointmentResponse result = AppointmentMapper.mapToProto(aiResponse);
            log.info("Appointment parsing completed successfully - patientId={}, doctorName={}",
                    result.getPatientId(), result.getDoctorName());

            return result;
        } catch (Exception e) {
            log.error("Appointment parsing failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse appointment: " + e.getMessage(), e);
        }
    }
}
