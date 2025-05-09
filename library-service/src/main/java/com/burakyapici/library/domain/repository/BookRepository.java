package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);
    boolean existsByTitle(String title);
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);
    @EntityGraph(attributePaths = {"authors", "genres", "bookCopies", "waitLists"})
    List<Book> findAll();
    Optional<Book> findByIdAndAuthors_Id(UUID id, UUID authorId);
}
