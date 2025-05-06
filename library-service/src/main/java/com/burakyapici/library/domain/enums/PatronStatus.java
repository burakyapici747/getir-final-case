package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum PatronStatus {
    ACTIVE("Patron membership is active and all services are available."),
    SUSPENDED("Patron borrowing privileges or other services have been temporarily suspended (e.g., due to outstanding fees or rule violations)."),
    INACTIVE("Patron membership has become inactive (e.g., expired membership or prolonged inactivity). May require reactivation."),
    BLOCKED("Patron account has been blocked due to serious or persistent policy violations.");

    private final String description;

    PatronStatus(String description) {
        this.description = description;
    }
}
