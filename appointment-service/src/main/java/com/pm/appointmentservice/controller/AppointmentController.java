package com.pm.appointmentservice.controller;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentResponseDto> getAppointmentByDateRange(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to) {
        log.info("GET /appointments - from={}, to={}", from, to);
        return appointmentService.getAppointmentByDateRange(from, to);
    }

    @PostMapping( "/ai-add/{id}")
    public ResponseEntity<AppointmentResponseDto> addAiTextAppointment(@RequestBody String text, @PathVariable UUID id){
        log.info("POST /appointments/ai-add/{} - received AI appointment text", id);
        AppointmentResponseDto responseDto=appointmentService.AiAddAppointment(text,id);
        log.info("AI appointment created: id={}", responseDto.getId());
        return ResponseEntity.ok().body(responseDto);
    }
}
