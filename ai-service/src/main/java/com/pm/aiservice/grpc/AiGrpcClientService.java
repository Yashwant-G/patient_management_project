package com.pm.aiservice.grpc;


import ai.AiServiceGrpc.AiServiceImplBase;
import ai.ParseAppointmentRequest;
import ai.ParseAppointmentResponse;
import com.pm.aiservice.service.AiParsingService;
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

        log.info("Recieved Ai-Service request: {}",request.toString());

        //logic

        ParseAppointmentResponse response=aiParsingService.parse(request.getText());

        responseObserver.onNext(response);

        responseObserver.onCompleted();

    }
}
