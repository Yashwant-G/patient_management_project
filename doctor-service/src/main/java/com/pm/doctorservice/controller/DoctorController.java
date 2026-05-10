package com.pm.doctorservice.controller;

import com.pm.doctorservice.dto.DoctorResponseDto;
import com.pm.doctorservice.dto.DoctorSearchDto;
import com.pm.doctorservice.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Doctor Management", description = "APIs for managing doctors")
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Search doctor by name using full-text search")
    @GetMapping("/search")
    public ResponseEntity<DoctorSearchDto> searchDoctorByName(@RequestParam String name) {
        log.info("GET /doctors/search?name={}", name);
        DoctorSearchDto result = doctorService.searchDoctorByName(name);
        log.info("Doctor found: id={}, name={}", result.getDoctorId(), result.getFullName());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get full doctor details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctorById(@PathVariable UUID id) {
        log.info("GET /doctors/{}", id);
        DoctorResponseDto result = doctorService.getDoctorById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get minimal doctor information by ID")
    @GetMapping("/{id}/minimal")
    public ResponseEntity<DoctorSearchDto> getDoctorMinimalById(@PathVariable UUID id) {
        log.info("GET /doctors/{}/minimal", id);
        DoctorSearchDto result = doctorService.getDoctorMinimalById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Validate doctor exists")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> doctorExists(@PathVariable UUID id) {
        log.info("GET /doctors/{}/exists", id);
        boolean exists = doctorService.exists(id);
        return ResponseEntity.ok(exists);
    }
}

