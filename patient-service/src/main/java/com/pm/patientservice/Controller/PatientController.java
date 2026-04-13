package com.pm.patientservice.Controller;

import com.pm.patientservice.DTO.PagedPatientDTO;
import com.pm.patientservice.DTO.PatientDTO;
import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.DTO.validators.CreatePatientValidationGroup;
import com.pm.patientservice.Service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient Management", description = "APIs for managing patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // /api/patients?page=1&size=10
    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<PagedPatientDTO> getPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "") String searchValue
    ) {
        log.info("GET /patients - page={}, size={}, sort={}, sortField={}, searchValue={}", page, size, sort, sortField, searchValue);
        PagedPatientDTO patients = patientService.getPatients(page, size, sort, sortField, searchValue);
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientDTO> createPatient(@Validated({Default.class,
            CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        log.info("POST /patients - Creating patient with email={}", patientRequestDTO.getEmail());
        PatientDTO patientDTO = patientService.createPatient(patientRequestDTO);
        log.info("Patient created successfully with id={}", patientDTO.getId());
        return ResponseEntity.ok().body(patientDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Patient")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable UUID id,
                                                    @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        log.info("PUT /patients/{} - Updating patient", id);
        PatientDTO patientDTO = patientService.updatePatient(id, patientRequestDTO);
        log.info("Patient updated successfully: id={}", id);
        return ResponseEntity.ok().body(patientDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient")
    public ResponseEntity<String> deletePatient(@PathVariable UUID id) {
        log.info("DELETE /patients/{} - Deleting patient", id);
        patientService.deletePatient(id);
        log.info("Patient deleted successfully: id={}", id);
        return ResponseEntity.ok().body("Patient Deleted Successfully: " + id);
    }
}

