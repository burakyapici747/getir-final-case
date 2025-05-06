package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {
    Optional<BookCopy> findByBarcode(UUID barcode);
}
