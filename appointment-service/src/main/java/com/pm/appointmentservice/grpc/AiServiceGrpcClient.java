package com.pm.appointmentservice.grpc;


import ai.AiServiceGrpc;
import ai.ParseAppointmentRequest;
import ai.ParseAppointmentResponse;
import com.pm.appointmentservice.dto.AppointmentResponseDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class AiServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(AiServiceGrpcClient.class);
    private final AiServiceGrpc.AiServiceBlockingStub blockingStub;

    public AiServiceGrpcClient(@Value("${ai.service.address:localhost}") String serverAddress,
                               @Value("${ai.service.grpc.port:9002}") int port) {
        log.info("Connecting to Ai service GRPC server at {}:{}",serverAddress,port);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, port).usePlaintext().build();
        blockingStub = AiServiceGrpc.newBlockingStub(channel);
    }

    public AppointmentResponseDto parseAppointment(String text) {
        log.info("Starting AI service gRPC call for text parsing");
        try {
            log.info("Sending Ai service request: {}", text);
            ParseAppointmentRequest request = ParseAppointmentRequest
                    .newBuilder()
                    .setText(text)
                    .build();

            ParseAppointmentResponse response = blockingStub.parseAppointment(request);
            log.info("Received Ai response successfully: {}", response.toString());

            AppointmentResponseDto appointmentResponseDto = new AppointmentResponseDto();
            appointmentResponseDto.setPatientId(UUID.fromString(response.getPatientId()));
            appointmentResponseDto.setPatientName(response.getPatientName());
            appointmentResponseDto.setDoctorName(response.getDoctorName());
            appointmentResponseDto.setStartTime(LocalTime.parse(response.getStartTime()));
            appointmentResponseDto.setEndTime(LocalTime.parse(response.getEndTime()));
            appointmentResponseDto.setReason(response.getReason());
            appointmentResponseDto.setAppointmentDate(LocalDate.parse(response.getAppointmentDate()));

            log.info("AI service gRPC call completed successfully");
            return appointmentResponseDto;
        } catch (StatusRuntimeException e) {
            log.error("AI service gRPC call failed with status: {}, message: {}", e.getStatus().getCode(), e.getMessage());
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                log.error("AI service is unavailable at configured endpoint. Please verify AI gRPC server health/connectivity.");
            }
            throw new RuntimeException("Failed to parse appointment from AI service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during AI service gRPC call: {}", e.getMessage(), e);
            throw new RuntimeException("AI service gRPC call failed: " + e.getMessage(), e);
        }
    }
}
