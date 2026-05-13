package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.pm.patientservice.Entity.Patient;
import com.pm.patientservice.kafka.kafkaProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private final kafkaProducer kafkaProducer;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int port, kafkaProducer kafkaProducer) {
        log.info("Connecting to Billing gRPC Service at {}:{}", serverAddress, port);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, port).usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
        this.kafkaProducer = kafkaProducer;
    }

    @CircuitBreaker(name = "billingService", fallbackMethod = "billingFallback")
    @Retry(name = "billingRetry")  //default retry is 3 times
    public BillingResponse createBillingAccount(Patient patient) {
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .build();

        log.info("Starting Billing service gRPC call for patientId={}", patient.getId());
        try {
            BillingResponse response = blockingStub.createBillingAccount(billingRequest);
            log.info("Billing service gRPC call completed - patientId={}, status={}", patient.getId(), response.getStatus());
            return response;
        } catch (StatusRuntimeException e) {
            log.error("Billing service gRPC call failed - status={}, patientId={}, error={}",
                    e.getStatus().getCode(), patient.getId(), e.getMessage());
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                log.error("Billing service is unavailable at configured endpoint. Please verify Billing gRPC server health/connectivity.");
            }
            throw e;
        }
    }

    public BillingResponse billingFallback(Patient patient, Throwable t) {
        log.warn("[CIRCUIT BREAKER]: Billing service is unavailable. Triggered Fallback: {}", t.getMessage());

        kafkaProducer.sendEvent(patient, "billing_topic", "BILLING_ACCOUNT_CREATE_REQUESTED");

        return BillingResponse.newBuilder()
                .setAccountId("XXX")
                .setStatus("Pending")
                .build();
    }
}
