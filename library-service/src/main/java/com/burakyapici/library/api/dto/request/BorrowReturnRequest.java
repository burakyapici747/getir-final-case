package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.ReturnType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BorrowReturnRequest(
    @NotNull(message = "Patron ID cannot be null")
    UUID patronId,

    @NotNull(message = "Return type cannot be null")
    ReturnType returnType
) {}