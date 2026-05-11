package com.pm.appointmentservice.grpc;

import doctor.DoctorServiceGrpc;
import doctor.DoctorEventRequest;
import doctor.DoctorEventResponse;
import doctor.SlotUpdateRequest;
import doctor.SlotUpdateResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(DoctorServiceGrpcClient.class);
    private final DoctorServiceGrpc.DoctorServiceBlockingStub blockingStub;

    public DoctorServiceGrpcClient(@Value("${doctor.service.address:localhost}") String serverAddress,
                                   @Value("${doctor.service.grpc.port:9003}") int port) {
        log.info("Connecting to Doctor service GRPC server at {}:{}", serverAddress, port);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, port).usePlaintext().build();
        blockingStub = DoctorServiceGrpc.newBlockingStub(channel);
    }

    public DoctorEventResponse checkSlotAvailability(String doctorId, String appointmentId, String appointmentDate,
                                                      String startTime, String endTime) {
        log.info("Sending Doctor service request for doctor_id: {}, appointment_id: {}", doctorId, appointmentId);

        DoctorEventRequest request = DoctorEventRequest
                .newBuilder()
                .setDoctorId(doctorId)
                .setAppointmentId(appointmentId)
                .setAppointmentDate(appointmentDate)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();

        DoctorEventResponse response = blockingStub.doctorEventSend(request);
        log.info("Received Doctor response: {}", response.toString());

        return response;
    }

    public SlotUpdateResponse updateSlotStatus(String slotId, String status) {
        log.info("Sending slot update request for slot_id: {}, status: {}", slotId, status);

        SlotUpdateRequest request = SlotUpdateRequest
                .newBuilder()
                .setSlotId(slotId)
                .setToStatus(status)
                .build();

        SlotUpdateResponse response = blockingStub.updateSlotEventSend(request);
        log.info("Received slot update response: {}", response.toString());

        return response;
    }
}

