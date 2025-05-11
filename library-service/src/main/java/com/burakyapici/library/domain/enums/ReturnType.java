package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum ReturnType {
    NORMAL(BookCopyStatus.AVAILABLE, BorrowStatus.RETURNED),
    DAMAGED(BookCopyStatus.IN_REPAIR, BorrowStatus.RETURNED_DAMAGED),
    LOST(BookCopyStatus.LOST, BorrowStatus.LOST);

    private final BookCopyStatus bookCopyStatus;
    private final BorrowStatus borrowStatus;

    ReturnType(BookCopyStatus bookCopyStatus, BorrowStatus borrowStatus) {
        this.bookCopyStatus = bookCopyStatus;
        this.borrowStatus = borrowStatus;
    }
}