package com.burakyapici.library.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorDto(
    UUID id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth
) {}
