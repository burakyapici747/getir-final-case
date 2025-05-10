package com.burakyapici.library.api.dto.request;

import java.time.LocalDate;

public record AuthorSearchCriteria(
    String firstName,
    String lastName,
    LocalDate dateOfBirth
) {}
