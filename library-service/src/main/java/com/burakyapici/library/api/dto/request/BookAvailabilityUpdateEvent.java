package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookAvailabilityUpdateEvent(
    @NotNull(message = "Book ID cannot be null")
    UUID bookId,
    @Min(value = 0, message = "Available count cannot be negative")
    int newAvailableCount
) {}