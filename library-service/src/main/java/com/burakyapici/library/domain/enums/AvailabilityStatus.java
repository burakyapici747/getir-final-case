package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum AvailabilityStatus {
    AVAILABLE("Available", "The book is available for borrowing"),
    BORROWED("Borrowed", "The book is currently borrowed by a patron"),
    RESERVED("Reserved", "The book is reserved for a patron"),
    MAINTENANCE("Maintenance", "The book is under maintenance or repair");

    private final String displayName;
    private final String description;

    AvailabilityStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public boolean isBorrowable() {
        return this == AVAILABLE;
    }

    public boolean isReservable() {
        return this == AVAILABLE || this == BORROWED;
    }
}