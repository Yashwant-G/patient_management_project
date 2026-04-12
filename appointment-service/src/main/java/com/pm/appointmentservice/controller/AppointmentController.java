package com.pm.appointmentservice.controller;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentResponseDto> getAppointmentByDateRange(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to) {
        return appointmentService.getAppointmentByDateRange(from, to);
    }

    @PostMapping( "/ai-add")
    public ResponseEntity<AppointmentResponseDto> addAiTextAppointment(@RequestBody String text){
        AppointmentResponseDto responseDto=appointmentService.AiAddAppointment(text);

        return ResponseEntity.ok().body(responseDto);
    }
}
