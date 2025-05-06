package com.burakyapici.library.domain.dto;

import java.util.Collection;

public record PageableDto<T> (
    Collection<T> elements,
    int totalPages,
    long totalElementsPerPage,
    int currentPage,
    boolean hasNext,
    boolean hasPrevious
) {}
