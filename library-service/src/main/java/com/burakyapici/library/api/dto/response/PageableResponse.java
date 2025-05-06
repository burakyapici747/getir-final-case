package com.burakyapici.library.api.dto.response;


import com.burakyapici.library.domain.dto.PageableDto;

import java.util.Collection;

public record PageableResponse<T>(
    Collection<T> elements,
    int totalPages,
    long totalElementsPerPage,
    int currentPage,
    boolean hasNext,
    boolean hasPrevious
) {
    public PageableResponse(PageableDto<T> pageableDTO) {
        this(
            pageableDTO.elements(),
            pageableDTO.totalPages(),
            pageableDTO.totalElementsPerPage(),
            pageableDTO.currentPage(),
            pageableDTO.hasNext(),
            pageableDTO.hasPrevious()
        );
    }
}