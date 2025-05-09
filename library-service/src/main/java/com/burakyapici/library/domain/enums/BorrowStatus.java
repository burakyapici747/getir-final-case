package com.burakyapici.library.domain.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    BORROWED,
    RETURNED,
    RETURNED_DAMAGED,
    OVERDUE,
    LOST;
}