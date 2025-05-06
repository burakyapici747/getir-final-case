package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    boolean existsByIsbn(String isbn);
    boolean existsByTitle(String title);
}
