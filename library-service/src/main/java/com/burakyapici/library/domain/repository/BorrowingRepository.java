package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> {
    @Query(value =
            """
                SELECT b.*
                FROM borrowing b
                JOIN book_copy bc ON b.book_copy_id = bc.id
                WHERE b.status = :status
                AND bc.barcode = :barcode
                AND b.user_id = :userId
                LIMIT 1;
            """,
            nativeQuery = true
    )
    Borrowing findByStatusAndBookCopyBarcodeAndUserId(
            @Param("status") String status,
            @Param("barcode") String barcode,
            @Param("userId") UUID userId
    );

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
    
    @Query(value = "SELECT COUNT(*) FROM borrowing WHERE user_id = :userId AND status = 'BORROWED'", nativeQuery = true)
    int countActiveByUserId(@Param("userId") UUID userId);
    
    @Query(value = 
        """
            SELECT COUNT(*) FROM borrowing b
            JOIN book_copy bc ON b.book_copy_id = bc.id
            JOIN book bk ON bc.book_id = bk.id
            WHERE b.user_id = :userId 
            AND b.status = 'BORROWED'
            AND bk.id = :bookId
        """, 
        nativeQuery = true)
    int countActiveByUserIdAndBookId(@Param("userId") UUID userId, @Param("bookId") UUID bookId);

    void deleteAllByBookCopyId(UUID bookCopyId);
}
