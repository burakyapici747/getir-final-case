package com.burakyapici.library.api.dto.request;

import java.time.LocalDate;

public record AuthorUpdateRequest(
    String firstName,
    String lastName,
    LocalDate dateOfBirth
) {}
