package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver) {
        log.info("Received createBillingAccount gRPC request for patient_id: {}", billingRequest.getPatientId());
        try {
            log.info("createBillingAccount request received: {}", billingRequest.toString());

            //Bussiness logic here
            //1. Create Billing table, and create PDF & send to notification service- to send mail to participants

            BillingResponse response = BillingResponse.newBuilder().
                    setAccountId(billingRequest.getPatientId()).
                    setStatus("NEW").
                    build();

            log.info("Sending createBillingAccount response for patient_id: {}", billingRequest.getPatientId());
            responseObserver.onNext(response);
            //can send multiple responses if needed

            responseObserver.onCompleted();
            log.info("createBillingAccount gRPC call completed successfully");
        } catch (Exception e) {
            log.error("Error processing createBillingAccount request: errorType={}, message={}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Billing service error: " + e.getMessage())
                    .asException());
        }
    }
}

