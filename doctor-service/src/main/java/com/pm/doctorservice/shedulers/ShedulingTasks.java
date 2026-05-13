package com.pm.doctorservice.shedulers;

import com.pm.doctorservice.entity.enums.SlotStatus;
import com.pm.doctorservice.repository.SlotsRepository;
import com.pm.doctorservice.service.SlotsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ShedulingTasks {

    private static final Logger log = LoggerFactory.getLogger(ShedulingTasks.class);
    private final SlotsService slotsService;

    public ShedulingTasks(SlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void CleanUpExpiredSlots(){
        log.info("Starting scheduled task: CleanUpExpiredSlots");
        slotsService.CleanUpExpiredSlotsService();
    }
}
