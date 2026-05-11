package com.pm.appointmentservice.grpc;

import payment.PaymentServiceGrpc;
import payment.PaymentRequest;
import payment.PaymentResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceGrpcClient.class);
    private final PaymentServiceGrpc.PaymentServiceBlockingStub blockingStub;

    public PaymentServiceGrpcClient(@Value("${payment.service.address:localhost}") String serverAddress,
                                    @Value("${payment.service.grpc.port:9004}") int port) {
        log.info("Connecting to Payment service GRPC server at {}:{}", serverAddress, port);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, port).usePlaintext().build();
        blockingStub = PaymentServiceGrpc.newBlockingStub(channel);
    }

    public PaymentResponse processPayment(String sagaId, String appointmentId, String patientName,
                                         String amount, String paymentMethod) {
        log.info("Sending Payment service request for saga_id: {}, appointment_id: {}", sagaId, appointmentId);

        PaymentRequest request = PaymentRequest
                .newBuilder()
                .setSagaId(sagaId)
                .setAppointmentId(appointmentId)
                .setPatientName(patientName)
                .setAmount(amount)
                .setPaymentMethod(paymentMethod)
                .build();

        PaymentResponse response = blockingStub.paymentEventSend(request);
        log.info("Received Payment response: {}", response.toString());

        return response;
    }
}

