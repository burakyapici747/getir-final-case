package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum ReturnType {
    LOST(BorrowStatus.LOST, BookCopyStatus.LOST),
    RETURNED_DAMAGED(BorrowStatus.RETURNED_DAMAGED, BookCopyStatus.IN_REPAIR),
    RETURNED(BorrowStatus.RETURNED, BookCopyStatus.AVAILABLE);

    private final BorrowStatus    borrowStatus;
    private final BookCopyStatus  bookCopyStatus;

    ReturnType(BorrowStatus bs, BookCopyStatus bcs) {
        this.borrowStatus   = bs;
        this.bookCopyStatus = bcs;
    }
}