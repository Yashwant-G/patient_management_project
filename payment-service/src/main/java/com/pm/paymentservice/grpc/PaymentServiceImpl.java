package com.pm.paymentservice.grpc;

import net.devh.boot.grpc.server.service.GrpcService;
import payment.PaymentServiceGrpc;
import payment.PaymentRequest;
import payment.PaymentResponse;
import io.grpc.stub.StreamObserver;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public void paymentEventSend(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        String sagaId = request.getSagaId();
        String appointmentId = request.getAppointmentId();
        String patientName = request.getPatientName();
        String amount = request.getAmount();
        String paymentMethod = request.getPaymentMethod();

        log.info("PAYMENT: Step-2 initiated - sagaId={}, appointmentId={}, patientName={}, amount={}, paymentMethod={}",
                sagaId, appointmentId, patientName, amount, paymentMethod);

        try {
            // Placeholder for payment processing logic
            String txnId = "TXN_" + request.getAppointmentId() + "_" + System.currentTimeMillis();
            String status = "FAILURE"; // Initially set to PENDING
            String message = "Network Down";

            PaymentResponse response = PaymentResponse
                    .newBuilder()
                    .setTxnId(txnId)
                    .setStatus(status)
                    .setMessage(message)
                    .build();

            log.info("PAYMENT: Sending response - txnId={}, status={}", txnId, status);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (NumberFormatException e) {
            log.error("PAYMENT: Amount parsing error - sagaId={}, amount={}, error={}", sagaId, amount, e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid amount format: " + e.getMessage())
                    .asException());

        } catch (Exception e) {
            log.error("PAYMENT: Unexpected error - sagaId={}, appointmentId={}, error={}", sagaId, appointmentId, e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Payment processing failed: " + e.getMessage())
                    .asException());
        }
    }
}

