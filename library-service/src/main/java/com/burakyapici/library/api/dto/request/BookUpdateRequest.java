package com.burakyapici.library.api.dto.request;

import java.util.Set;
import java.util.UUID;

public record BookUpdateRequest(
    String title,
    String isbn,
    int page,
    Set<UUID> waitListIds,
    Set<UUID> authorIds,
    Set<UUID> genreIds
) {}
