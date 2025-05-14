package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum ReturnType {
    NORMAL(BookCopyStatus.AVAILABLE, BorrowingStatus.RETURNED),
    DAMAGED(BookCopyStatus.IN_REPAIR, BorrowingStatus.RETURNED_DAMAGED),
    LOST(BookCopyStatus.LOST, BorrowingStatus.LOST);

    private final BookCopyStatus bookCopyStatus;
    private final BorrowingStatus borrowingStatus;

    ReturnType(BookCopyStatus bookCopyStatus, BorrowingStatus borrowingStatus) {
        this.bookCopyStatus = bookCopyStatus;
        this.borrowingStatus = borrowingStatus;
    }
}