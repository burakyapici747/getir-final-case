package com.burakyapici.library.api.dto.request;

import java.util.UUID;

public record BorrowReturnRequest(
    UUID bookCopyId,
    UUID patronId,
    boolean damageReportedDuringReturn,
    String damageNotesDuringReturn,
    boolean isLost
) {}
