package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("Pending", "Reservation request is waiting for approval", false),
    CONFIRMED("Confirmed", "Reservation has been approved and book is being held", false),
    CANCELLED("Cancelled", "Reservation was cancelled by patron or librarian", true),
    EXPIRED("Expired", "Reservation period has ended without pickup", true);

    private final String displayName;
    private final String description;
    private final boolean isTerminated;

    ReservationStatus(String displayName, String description, boolean isTerminated) {
        this.displayName = displayName;
        this.description = description;
        this.isTerminated = isTerminated;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getStatusCode() {
        return "RESERVATION_" + this.name();
    }
}