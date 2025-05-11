package com.burakyapici.library.api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorResponse(
    UUID id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth
) {}
