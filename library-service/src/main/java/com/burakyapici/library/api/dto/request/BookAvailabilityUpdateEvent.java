package com.burakyapici.library.api.dto.request;

import java.util.UUID;

public record BookAvailabilityUpdateEvent(
    UUID bookId,
    int newAvailableCount
) {}
