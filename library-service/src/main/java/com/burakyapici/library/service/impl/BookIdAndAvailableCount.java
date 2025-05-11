package com.burakyapici.library.service.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookIdAndAvailableCount {
    private final UUID bookId;
    private final long availableCount;

    public BookIdAndAvailableCount(UUID bookId, long availableCount) {
        this.bookId = bookId;
        this.availableCount = availableCount;
    }
}