package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    boolean existsByFirstName(String firstName);
    Page<Author> findAll(Specification<Author> specification, Pageable pageable);

    @Query(value =
       """
        DELETE FROM book_author
        WHERE author_id = :authorId
       """
    , nativeQuery = true)
    @Modifying
    void deleteBookAuthorByAuthorId(@Param("authorId") UUID authorId);
    boolean existsById(UUID id);
}
