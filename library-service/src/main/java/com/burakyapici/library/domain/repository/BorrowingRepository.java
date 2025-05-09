package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Borrowing findByStatusAndBookCopy_BarcodeAndUser_Id(BorrowStatus status, String barcode, UUID userId);
}
