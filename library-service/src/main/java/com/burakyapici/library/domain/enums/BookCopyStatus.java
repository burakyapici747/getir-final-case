package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum BookCopyStatus {
    AVAILABLE("The book is on the shelf and ready to be borrowed."),
    CHECKED_OUT("The book is currently checked out by a patron."),
    ON_HOLD("The book is reserved for a specific patron and awaiting pickup at the library."),
    IN_REPAIR("The book is physically damaged and being repaired."),
    LOST("The book cannot be found and has been declared lost."),
    WITHDRAWN("The book has been permanently removed from the collection."),
    MISSING("The book cannot be found on the shelf or in its expected location, may be temporarily misplaced.");

    private final String description;

    BookCopyStatus(String description) {
        this.description = description;
    }
}