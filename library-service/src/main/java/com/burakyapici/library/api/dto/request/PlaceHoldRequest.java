package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PlaceHoldRequest(
    @NotNull(message = "Book ID cannot be null")
    UUID bookId
) {}