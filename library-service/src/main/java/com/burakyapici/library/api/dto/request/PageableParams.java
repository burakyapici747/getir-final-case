package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.RequestParam;

public record PageableParams(
    @RequestParam(name = "page", required = false)
    @Min(value = 0, message = "Page must be 0 or greater")
    Integer page,

    @RequestParam(name = "size", required = false)
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 50, message = "Size must be at most 50")
    Integer size
) {
    public PageableParams {
        if (page == null) page = 0;
        if (size == null) size = 10;
    }
}