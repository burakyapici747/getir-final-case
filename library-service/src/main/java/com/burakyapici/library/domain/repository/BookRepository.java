package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.WaitList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    @EntityGraph("Book")
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    @EntityGraph("Book")
    Page<Book> findAll(Pageable pageable);

    @EntityGraph("Book.detail")
    Optional<Book> findById(UUID id);

    Optional<Book> findByIdAndAuthors_Id(UUID id, UUID authorId);

    @Query("SELECT w FROM Book b JOIN b.waitLists w WHERE b.id = :bookId AND w.status IN :status")
    Optional<WaitList> findByBookIdAndStatusIn(
        @Param("bookId") UUID bookId,
        @Param("status") Collection<WaitListStatus> status
    );

    @Query("SELECT w FROM Book b JOIN b.waitLists w WHERE b.id = :bookId")
    Page<WaitList> findById(UUID bookId, Pageable pageable);

    @EntityGraph("Book")
    List<Book> findAllByAuthors_Id(UUID authorId);

    @Query(value =
        """
            SELECT CASE WHEN count(b)>0 THEN TRUE ELSE FALSE END
            FROM Book b
            WHERE b.isbn = :isbn or b.title = :title
        """
    )
    boolean existsByIsbnOrTitle(
        @Param("isbn") String isbn,
        @Param("title") String title
    );

    boolean existsById(UUID id);
}

