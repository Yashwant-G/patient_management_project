package com.pm.appointmentservice.client;

import com.pm.appointmentservice.dto.DoctorClientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class DoctorServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DoctorServiceClient.class);

    private final WebClient webClient;
    private final String doctorServiceUrl;

    public DoctorServiceClient(WebClient webClient, @Value("${doctor.service.url:http://localhost:4007}") String doctorServiceUrl) {
        this.webClient = webClient;
        this.doctorServiceUrl = doctorServiceUrl;
    }

    /**
     * Search for a doctor by name using full-text search
     */
    public DoctorClientDto searchDoctorByName(String name) {
        log.info("Calling Doctor Service to search for doctor by name: {}", name);
        
        DoctorClientDto response = webClient.get()
                .uri(doctorServiceUrl + "/doctors/search?name={name}", name)
                .retrieve()
                .bodyToMono(DoctorClientDto.class)
                .block();
        
        if (response != null) {
            log.info("Doctor found from Doctor Service: id={}, name={}", response.getDoctorId(), response.getFullName());
        }
        return response;
    }

    /**
     * Get minimal doctor information by ID
     */
    public DoctorClientDto getDoctorMinimalById(UUID doctorId) {
        log.info("Calling Doctor Service to fetch minimal doctor info for id={}", doctorId);
        
        DoctorClientDto response = webClient.get()
                .uri(doctorServiceUrl + "/doctors/{id}/minimal", doctorId)
                .retrieve()
                .bodyToMono(DoctorClientDto.class)
                .block();
        
        if (response != null) {
            log.info("Doctor info fetched from Doctor Service: id={}, name={}", response.getDoctorId(), response.getFullName());
        }
        return response;
    }

    /**
     * Check if doctor exists
     */
    public boolean doctorExists(UUID doctorId) {
        log.info("Calling Doctor Service to check if doctor exists: id={}", doctorId);
        
        Boolean exists = webClient.get()
                .uri(doctorServiceUrl + "/doctors/{id}/exists", doctorId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        
        log.info("Doctor exists check result: id={}, exists={}", doctorId, exists);
        return exists != null ? exists : false;
    }

    /**
     * Get full doctor details by ID
     */
    public DoctorClientDto getDoctorById(UUID doctorId) {
        log.info("Calling Doctor Service to fetch full doctor details for id={}", doctorId);
        
        DoctorClientDto response = webClient.get()
                .uri(doctorServiceUrl + "/doctors/{id}", doctorId)
                .retrieve()
                .bodyToMono(DoctorClientDto.class)
                .block();
        
        if (response != null) {
            log.info("Full doctor details fetched from Doctor Service: id={}, name={}", response.getDoctorId(), response.getFullName());
        }
        return response;
    }
}

