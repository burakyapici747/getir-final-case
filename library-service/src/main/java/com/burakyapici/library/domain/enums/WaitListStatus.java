package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum WaitListStatus {
    WAITING("Patron is in queue waiting for a copy of the book to become available."),
    READY_FOR_PICKUP("A copy of the book has been reserved for this patron and is awaiting pickup at the library."),
    COMPLETED("Patron has successfully borrowed the reserved book."),
    EXPIRED("Patron did not pick up the reserved book within the designated pickup period."),
    CANCELLED("The waiting request has been cancelled by the patron or staff.");

    private final String description;

    WaitListStatus(String description) {
        this.description = description;
    }
}