package com.burakyapici.library.api.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record BorrowBookCopyRequest(
    @NotNull(message = "Patron ID cannot be null")
    UUID patronId
) {}