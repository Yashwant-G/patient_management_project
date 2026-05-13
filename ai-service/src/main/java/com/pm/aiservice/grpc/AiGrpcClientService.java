package com.pm.aiservice.grpc;


import ai.AiServiceGrpc.AiServiceImplBase;
import ai.ParseAppointmentRequest;
import ai.ParseAppointmentResponse;
import com.pm.aiservice.service.AiParsingService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class AiGrpcClientService extends AiServiceImplBase {
    private final AiParsingService aiParsingService;

    private static final Logger log = LoggerFactory.getLogger(AiGrpcClientService.class);

    public AiGrpcClientService(AiParsingService aiParsingService) {
        this.aiParsingService = aiParsingService;
    }

    @Override
    public void parseAppointment(ParseAppointmentRequest request, StreamObserver<ParseAppointmentResponse> responseObserver) {
        log.info("Received parseAppointment gRPC request for appointment parsing");
        try {
            log.info("Received AI-Service request: {}", request.toString());

            //logic
            log.info("Starting appointment text parsing via AI service");
            ParseAppointmentResponse response = aiParsingService.parse(request.getText());
            log.info("Appointment parsing completed - patientId={}, doctorName={}", response.getPatientId(), response.getDoctorName());

            log.info("Sending parseAppointment gRPC response");
            responseObserver.onNext(response);

            responseObserver.onCompleted();
            log.info("parseAppointment gRPC call completed successfully");
        } catch (Exception e) {
            log.error("Error processing parseAppointment request: errorType={}, message={}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("AI service parsing error: " + e.getMessage())
                    .asException());
        }
    }
}
