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
        String prompt=appointmentPrompt.buildPrompt(text);

        log.info("prompt built: {}",prompt);

        String aiResponse=chatConfig.call(prompt);

        log.info("Recieved raw Ai Response: {}",aiResponse);

        return AppointmentMapper.mapToProto(aiResponse);
    }
}
