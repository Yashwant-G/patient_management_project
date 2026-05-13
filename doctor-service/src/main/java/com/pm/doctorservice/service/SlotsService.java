package com.pm.doctorservice.service;

import com.pm.doctorservice.entity.Doctor;
import com.pm.doctorservice.entity.Slots;
import com.pm.doctorservice.entity.enums.SlotStatus;
import com.pm.doctorservice.exception.SlotNotFoundException;
import com.pm.doctorservice.repository.SlotsRepository;
import doctor.DoctorEventRequest;
import doctor.SlotUpdateRequest;
import doctor.SlotUpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class SlotsService {

    private static final Logger log = LoggerFactory.getLogger(SlotsService.class);
    private final SlotsRepository slotsRepository;

    public SlotsService(SlotsRepository slotsRepository) {
        this.slotsRepository = slotsRepository;
    }

    UUID validateAppointmentSlot(DoctorEventRequest doctorEventRequest, Doctor doctor){
        //validate slot exists
        LocalDate slotDate = LocalDate.parse(doctorEventRequest.getAppointmentDate());
        LocalTime startTime = LocalTime.parse(doctorEventRequest.getStartTime());
        LocalTime endTime = LocalTime.parse(doctorEventRequest.getEndTime());

        if(slotsRepository.existsByDoctor_DoctorIdAndSlotDateAndStartTime(
                UUID.fromString(doctorEventRequest.getDoctorId()),
                slotDate,
                startTime)){
            log.error("Doctor Slot already exists for request slot: {}",doctorEventRequest.getStartTime());
            throw new SlotNotFoundException("Doctor Slot already exists for request slot");
        }

        Slots slots=new Slots();
        slots.setAppointmentId(UUID.fromString(doctorEventRequest.getAppointmentId()));
        slots.setDoctor(doctor);
        slots.setSlotDate(slotDate);
        slots.setStartTime(startTime);
        slots.setEndTime(endTime);
        slots.setStatus(SlotStatus.HELD);
        slots.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        log.info("Creating new slot for doctor_id={}, slot_date={}, start_time={}, end_time={}",
                doctorEventRequest.getDoctorId(), slotDate, startTime, endTime);

        return slotsRepository.save(slots).getSlotId();
    }

    @Transactional
    public SlotUpdateResponse updateSlot(SlotUpdateRequest request){
        Slots slot=slotsRepository.findById(UUID.fromString(request.getSlotId())).orElseThrow(
                () -> new SlotNotFoundException("Slot not found to update with id: "+request.getSlotId())
        );

        log.info("Updating slot status for slot_id={}, new_status={}", request.getSlotId(), request.getToStatus());

        // Get doctor name before any potential deletion
        String doctorName = slot.getDoctor().getFullName();

        if(request.getToStatus().equalsIgnoreCase("Success")){
            slot.setStatus(SlotStatus.BOOKED);
            slotsRepository.save(slot);
            log.info("Slot status updated to BOOKED for slot_id={}", request.getSlotId());
        }
        else if(request.getToStatus().equalsIgnoreCase("Failure")){
            slot.setStatus(SlotStatus.CANCELLED);
            slotsRepository.delete(slot);
            log.info("Slot deleted due to payment failure for slot_id={}", request.getSlotId());
        }
        else {
            log.warn("Unknown slot status: {} for slot_id={}", request.getToStatus(), request.getSlotId());
        }

        return SlotUpdateResponse.newBuilder()
                .setDoctorName(doctorName)
                .setSuccess("DONE")
                .build();
    }

    @Transactional
    public void CleanUpExpiredSlotsService(){
        try{
            LocalDateTime now=LocalDateTime.now();
            log.info("Attempting deleting slots with expiry before now: {}",now );
            Long rows=slotsRepository.deleteAllByExpiresAtBeforeAndStatus(now, SlotStatus.HELD);
            log.info("Successfully cleaned up {} rows of HELD slots", rows);
        } catch (Exception e){
            log.error("Error while cleaning up expired slots", e);
            throw new RuntimeException(e);
        }
    }

}
