package com.burakyapici.library.api.dto.response;

import java.util.UUID;

public record GenreResponse(
    UUID id,
    String name,
    String description
) {}
