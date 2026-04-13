package com.pm.aiservice.prompt;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentPrompt {
    public String buildPrompt(String input) {
        String today= LocalDateTime.now().toString();
        return """
        You are a strict JSON generator.

        Current date: %s

        RULES:
        - Output ONLY valid JSON
        - No explanation, no markdown
        - All fields must exist
        - end_time must be in Future
        - Missing values → ""

        FORMAT:
        {
          "patient_id": "",
          "start_time": "",
          "end_time": "",
          "reason": "",
          "patient_name": "",
          "doctor_name": ""
        }

        LOGIC:
        - Convert time to ISO: yyyy-MM-dd'T'HH:mm:ss
        - If end_time missing → start_time + 30 minutes
        - patient_name: extract the patient's full name from the input
        - doctor_name: extract the doctor's name exactly as mentioned in the input

        INPUT:
        %s
        """.formatted(today,input);
    }
}
