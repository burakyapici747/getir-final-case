package com.burakyapici.library.domain.dto;

import java.time.LocalDate;

public record AuthorDto(
    String id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth
) {}
