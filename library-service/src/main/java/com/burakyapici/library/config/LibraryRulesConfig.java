package com.burakyapici.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "library.rules")
public class LibraryRulesConfig {
    private int maxBooksPerPatron = 5;
    private int maxLoanPeriodDays = 15;
    private int maxRenewalCount = 2;
    private int renewalPeriodDays = 7;

    private int maxReservationsPerPatron = 3;
    private int maxReservationHoldDays = 3;
    private boolean autoProcessWaitList = true;
    
    private double overdueFinePerDay = 1.0;
    private double damagedBookPenalty = 50.0;
    private double lostBookPenalty = 100.0;
    private int gracePeriodDays = 1;
    private double maxFineAmount = 200.0;

    private double blockThresholdAmount = 30.0;
    private int inactivityMonths = 12;

    private int overdueCheckHour = 1;
    private int waitListProcessingHour = 2;
    private int inactiveUserCheckDay = 1;
} 