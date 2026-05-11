package com.pm.appointmentservice.service;

import com.pm.appointmentservice.dto.AppointmentResponseDto;
import com.pm.appointmentservice.entity.Appointment;
import com.pm.appointmentservice.entity.enums.AppointmentStatus;
import com.pm.appointmentservice.grpc.DoctorServiceGrpcClient;
import com.pm.appointmentservice.grpc.PaymentServiceGrpcClient;
import com.pm.appointmentservice.repository.AppointmentRepository;
import doctor.DoctorEventResponse;
import doctor.SlotUpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.PaymentResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SagaOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestratorService.class);
    private final AppointmentRepository appointmentRepository;
    private final DoctorServiceGrpcClient doctorServiceGrpcClient;
    private final PaymentServiceGrpcClient paymentServiceGrpcClient;

    public SagaOrchestratorService(AppointmentRepository appointmentRepository, DoctorServiceGrpcClient doctorServiceGrpcClient, PaymentServiceGrpcClient paymentServiceGrpcClient) {
        this.appointmentRepository = appointmentRepository;
        this.doctorServiceGrpcClient = doctorServiceGrpcClient;
        this.paymentServiceGrpcClient = paymentServiceGrpcClient;
    }

    @Transactional
    public AppointmentResponseDto startSaga(Appointment appointment) {
        UUID sagaId = UUID.randomUUID();
        appointment.setSagaId(sagaId);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        log.info("[SAGA-{}] Saga started - appointmentId={}, doctorId={}, appointmentDate={}",
                sagaId, appointment.getAppointmentId(), appointment.getDoctorId(), appointment.getAppointmentDate());

        appointmentRepository.save(appointment);
        log.debug("[SAGA-{}] Appointment saved in PENDING status", sagaId);

        //Step 1: Send Doctor event - Check Slot Availability
        log.info("[SAGA-{}] Step-1: Checking doctor slot availability", sagaId);
        DoctorEventResponse response = doctorServiceGrpcClient.checkSlotAvailability(
                appointment.getDoctorId().toString(),
                appointment.getAppointmentId().toString(),
                appointment.getAppointmentDate().toString(),
                appointment.getStartTime().toString(),
                appointment.getEndTime().toString()
        );
        log.debug("[SAGA-{}] Slot availability response received - fees={}, slotId={}",
                sagaId, response.getFees(), response.getSlotId());

        appointment.setAmount(new BigDecimal(response.getFees()));
        appointment.setSlotId(UUID.fromString(response.getSlotId()));

        //Step 2: Send payment event
        log.info("[SAGA-{}] Step-2: Processing payment - amount={}, method={}",
                sagaId, appointment.getAmount(), appointment.getPaymentMethod());
        appointment.setAppointmentStatus(AppointmentStatus.PAYMENT_PENDING);

        PaymentResponse paymentResponse = paymentServiceGrpcClient.processPayment(
                appointment.getSagaId().toString(),
                appointment.getAppointmentId().toString(),
                appointment.getCachedPatient().getFullName(),
                appointment.getAmount().toString(),
                appointment.getPaymentMethod().name());

        log.debug("[SAGA-{}] Payment response received - status={}, txnId={}",
                sagaId, paymentResponse.getStatus(), paymentResponse.getTxnId());

        String paymentStatus = paymentResponse.getStatus();
        appointment.setTxnId(paymentResponse.getTxnId());
        String status;

        //Step 3: Update slot status based on payment outcome
        if (paymentStatus.equalsIgnoreCase("success")) {
            log.info("[SAGA-{}] Step-3: Payment successful, updating slot status to SUCCESS", sagaId);
            SlotUpdateResponse slotUpdateResponse = doctorServiceGrpcClient.updateSlotStatus(appointment.getSlotId().toString(), "Success");
            appointment.setDoctorName(slotUpdateResponse.getDoctorName());
            appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
            status="Appointment Booking Successful";
            log.info("[SAGA-{}] Saga completed successfully - appointmentId={}, doctorName={}",
                    sagaId, appointment.getAppointmentId(), appointment.getDoctorName());
        } else {
            log.warn("[SAGA-{}] Step-3: Payment failed, updating slot status to FAILURE", sagaId);
            SlotUpdateResponse slotUpdateResponse = doctorServiceGrpcClient.updateSlotStatus(appointment.getSlotId().toString(), "Failure");
            appointment.setDoctorName(slotUpdateResponse.getDoctorName());
            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
            status="Appointment Booking failed due to Payment_Failed with TXN_ID: "+appointment.getTxnId()+" message: "+paymentResponse.getStatus();
            log.warn("[SAGA-{}] Saga failed - reason: Payment declined, txnId={}", sagaId, appointment.getTxnId());
        }

        appointmentRepository.save(appointment);
        log.debug("[SAGA-{}] Final appointment status saved", sagaId);

        return mapTo(appointment,status);
    }

    public AppointmentResponseDto mapTo(Appointment appointment, String status) {

        return new AppointmentResponseDto(
                appointment.getAppointmentId(),
                appointment.getPatientId(),
                appointment.getCachedPatient().getFullName(),
                appointment.getDoctorName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                status,
                appointment.getReason(),
                appointment.getVersion()
        );
    }
}
