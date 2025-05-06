package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum BookStatus {
    ACTIVE("The book is part of the collection and copies are available for borrowing/use."),
    ARCHIVED("The book is still in the catalog but is no longer actively managed or acquired. Remaining copies may still be available for borrowing/use."),
    WITHDRAWN("The book and all its copies have been permanently removed from the collection. The catalog record may be retained for historical information."),
    ON_ORDER("The book is in the ordering process and has not yet arrived at the library.");

    private final String description;

    BookStatus(String description) {
        this.description = description;
    }
}
