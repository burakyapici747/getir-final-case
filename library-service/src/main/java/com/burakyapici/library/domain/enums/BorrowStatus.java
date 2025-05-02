package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    ACTIVE("Active", "The book is currently borrowed and within the loan period"),
    RETURNED("Returned", "The book has been returned to the library"),
    OVERDUE("Overdue", "The book's loan period has expired and has not been returned");

    private final String displayName;
    private final String description;

    BorrowStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE || this == OVERDUE;
    }

    public boolean requiresAction() {
        return this == OVERDUE;
    }

    public boolean isCompleted() {
        return this == RETURNED;
    }
}