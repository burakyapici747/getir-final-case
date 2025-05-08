package com.burakyapici.library.api.dto.request;

import java.time.LocalDate;

public record AuthorSearchCriteria(
    String firstname,
    String lastname,
    LocalDate dateOfBirth
) {}
