package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookCopyRepository extends JpaRepository<BookCopy, UUID>, JpaSpecificationExecutor<BookCopy> {
    Optional<BookCopy> findByBarcode(String barcode);
    Page<BookCopy> findAllByBookId(UUID bookId, Pageable pageable);
    boolean existsByBarcode(String barcode);
    int countByBook_IdAndStatus(UUID bookId, @NotNull BookCopyStatus status);
    Page<BookCopy> findAll(Specification<BookCopy> specification, Pageable pageable);

    @Modifying
    @Query(value =
           """
               DELETE FROM book_copy
               WHERE book_id = :bookId
           """,
        nativeQuery = true
    )
    void deleteByBookId(@Param(value = "bookId") UUID bookId);

    @Query(value =
        """
            SELECT * FROM book_copy 
            WHERE book_id = :bookId 
            AND availability_status = :status
        """,
            nativeQuery = true
    )
    List<BookCopy> findByBookIdAndStatus(
        @Param("bookId") UUID bookId,
        @Param("status") String status
    );
}
