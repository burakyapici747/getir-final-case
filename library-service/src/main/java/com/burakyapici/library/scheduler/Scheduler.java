package com.burakyapici.library.scheduler;

import com.burakyapici.library.service.BorrowingService;
import com.burakyapici.library.service.WaitListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final BorrowingService borrowingService;
    private final WaitListService waitListService;

    public Scheduler(BorrowingService borrowingService, WaitListService waitListService) {
        this.borrowingService = borrowingService;
        this.waitListService = waitListService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void processDailyLibraryTasks() {
        log.info("Starting midnight library tasks...");

        try {
            log.info("Checking overdue borrowings...");
            borrowingService.processOverdueBorrowings();
            log.info("Overdue borrowings processing completed.");
        } catch (Exception e) {
            log.error("An error occurred while processing overdue borrowings: ", e);
        }

        try {
            log.info("Checking expired waitlist entries...");
            waitListService.processExpiredWaitListEntries();
            log.info("Expired waitlist entries processing completed.");
        } catch (Exception e) {
            log.error("An error occurred while processing expired waitlist entries: ", e);
        }

        log.info("Midnight library tasks completed.");
    }
}
