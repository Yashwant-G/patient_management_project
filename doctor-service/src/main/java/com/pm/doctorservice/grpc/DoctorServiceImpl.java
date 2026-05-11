package com.pm.doctorservice.grpc;

import com.pm.doctorservice.service.DoctorService;
import com.pm.doctorservice.service.SlotsService;
import doctor.DoctorServiceGrpc;
import doctor.DoctorEventRequest;
import doctor.DoctorEventResponse;
import doctor.SlotUpdateRequest;
import doctor.SlotUpdateResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class DoctorServiceImpl extends DoctorServiceGrpc.DoctorServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(DoctorServiceImpl.class);
    private final DoctorService doctorService;
    private final SlotsService slotsService;

    public DoctorServiceImpl(DoctorService doctorService, SlotsService slotsService) {
        super();
        this.doctorService = doctorService;
        this.slotsService = slotsService;
    }

    @Override
    public void doctorEventSend(DoctorEventRequest request, StreamObserver<DoctorEventResponse> responseObserver) {
        log.info("Received DoctorEventRequest - doctor_id: {}, appointment_id: {}, appointment_date: {}, start_time: {}, end_time: {}",
                request.getDoctorId(), request.getAppointmentId(), request.getAppointmentDate(),
                request.getStartTime(), request.getEndTime());

        try {
            DoctorEventResponse response = doctorService.validateDoctorEvent(request);

            log.info("Sending DoctorEventResponse - slot_id: {}, fees: {}", response.getSlotId(), response.getFees());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error processing DoctorEventRequest: errorType={}, message={}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Doctor service error: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void updateSlotEventSend(SlotUpdateRequest request, StreamObserver<SlotUpdateResponse> responseObserver) {
        log.info("Received SlotUpdateRequest - slot_id: {}, to_status: {}", request.getSlotId(), request.getToStatus());

        try {
            SlotUpdateResponse response = slotsService.updateSlot(request);

            log.info("Sending SlotUpdateResponse - doctor_name: {}, success: {}", response.getDoctorName(), response.getSuccess());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error processing SlotUpdateRequest: errorType={}, message={}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Slot update error: " + e.getMessage())
                    .asException());
        }
    }
}

