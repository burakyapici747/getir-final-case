package com.burakyapici.library.service.impl;

import java.util.UUID;

public class BookIdAndAvailableCount {
    private final UUID bookId;
    private final long availableCount; // Sayım Long döner

    public BookIdAndAvailableCount(UUID bookId, long availableCount) {
        this.bookId = bookId;
        this.availableCount = availableCount;
    }

    public UUID getBookId() {
        return bookId;
    }

    public long getAvailableCount() {
        return availableCount;
    }
}