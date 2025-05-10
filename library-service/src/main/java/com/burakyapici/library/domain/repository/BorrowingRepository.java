package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    @Query(value =
        """
            SELECT *
            FROM borrowing b
            JOIN book_copy bc ON b.book_copy_id = bc.id
            WHERE b.status = :status
            AND bc.barcode = :barcode
            AND b.user_id = :userId
            LIMIT 1;
        """,
            nativeQuery = true)
    Borrowing findByStatusAndBookCopyBarcodeAndUserId(@Param("status") BorrowStatus status,@Param("barcode") String barcode,@Param("userId")  UUID userId);

    @Modifying
    @Query(value =
        """
            DELETE FROM borrowing
            WHERE book_copy_id IN (
                SELECT id FROM book_copy WHERE book_id = :bookId
            )
        """,
        nativeQuery = true
    )
    void deleteAllByBookCopyBookId(@Param("bookId") UUID bookId);
}
