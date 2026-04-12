package com.pm.appointmentservice.grpc;


import ai.AiServiceGrpc;
import ai.ParseAppointmentRequest;
import ai.ParseAppointmentResponse;
import com.pm.appointmentservice.dto.AppointmentResponseDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        log.info("sending Ai service request: {}",text);
        ParseAppointmentRequest request = ParseAppointmentRequest
                .newBuilder()
                .setText(text)
                .build();

        ParseAppointmentResponse response=blockingStub.parseAppointment(request);
        log.info("Received Ai response: {}",response.toString());

        AppointmentResponseDto appointmentResponseDto=new AppointmentResponseDto();
        appointmentResponseDto.setPatientId(UUID.fromString(response.getPatientId()));
        appointmentResponseDto.setStartTime(LocalDateTime.parse(response.getStartTime()));
        appointmentResponseDto.setEndTime(LocalDateTime.parse(response.getEndTime()));
        appointmentResponseDto.setReason(response.getReason());

        return appointmentResponseDto;
    }
}
