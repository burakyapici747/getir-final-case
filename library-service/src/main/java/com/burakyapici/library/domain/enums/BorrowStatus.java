package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    BORROWED("The item is currently checked out by a patron."),
    RETURNED("The item has been returned in good condition."),
    RETURNED_DAMAGED("The item has been returned but is damaged."),
    OVERDUE("The item's due date has passed but it has not been returned."),
    LOST("The item has been declared lost by the patron or staff.");
    
    private final String description;
    
    BorrowStatus(String description) {
        this.description = description;
    }
}