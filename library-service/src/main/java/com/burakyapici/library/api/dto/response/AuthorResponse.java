package com.burakyapici.library.api.dto.response;

import java.time.LocalDate;

public record AuthorResponse(
    String firstName,
    String lastName,
    LocalDate dateOfBirth
) {}
