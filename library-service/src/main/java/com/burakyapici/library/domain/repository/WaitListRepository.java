package com.burakyapici.library.domain.repository;

import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.WaitList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface WaitListRepository extends JpaRepository<WaitList, UUID> {
    @Query(value =
        """
            SELECT EXISTS (
                SELECT 1
                FROM wait_list wl
                WHERE wl.book_id = :bookId
                LIMIT 1
            )
        """,
        nativeQuery = true
    )
    boolean existsByBook_Id(@Param(value = "book_id") UUID bookId);

    @Query(value =
        """
            SELECT * FROM wait_list wl
            WHERE wl.book_id = :bookId
        """,
        countQuery =
        """
            SELECT COUNT(*) FROM wait_list wl
            WHERE wl.book_id = :bookId
        """,
        nativeQuery = true
    )
    Page<WaitList> findByBookId(@Param("bookId") UUID bookId, Pageable pageable);

    @Query(value =
            """
                SELECT * FROM wait_list wl
                WHERE wl.book_id = :bookId
                AND wl.user_id = :userId
                AND wl.status IN (:waitListStatuses)
            """,
            nativeQuery = true
    )
    Optional<WaitList> findWaitListByBookIdAndUserIdAndWaitListStatusIn(
        @Param("bookId") UUID bookId,
        @Param("userId") UUID userId,
        @Param("waitListStatuses") Set<String> waitListStatuses
    );

    @Query(
            value = """
        SELECT * FROM wait_list
        WHERE book_id = :bookId
        """,
            countQuery = """
        SELECT COUNT(*) FROM wait_list
        WHERE book_id = :bookId
        """,
            nativeQuery = true
    )
    Page<WaitList> findAllByBookId(@Param("bookId") UUID bookId, Pageable pageable);

    List<WaitList> findByUser_Id(UUID id);

    @Query(value =
            """
                SELECT * FROM wait_list wl
                WHERE wl.book_id = :bookId
                AND wl.status = :status
            """,
            nativeQuery = true
    )
    List<WaitList> findByBookIdAndStatus(@Param("bookId") UUID bookId, @Param("status") WaitListStatus status);

    List<WaitList> findByUser_IdAndStatusIn(UUID userId, Collection<WaitListStatus> statuses);

    @Query(value =
        """
            SELECT * FROM wait_list wl
            WHERE wl.user_id = :userId
            AND wl.book_id = :bookId
            AND wl.status = :status
        """,
        nativeQuery = true
    )
    Optional<WaitList> findByUserIdAndBookIdAndStatus(
        @Param(value = "userId")
        UUID userId,
        @Param(value = "bookId")
        UUID bookId,
        @Param(value = "status")
        String status
    );


    @Modifying
    @Query(value =
        """
        DELETE FROM wait_list wl
        WHERE wl.book_id = :bookId
        """
    , nativeQuery = true)
    void deleteByBookId(@Param(value = "bookId") UUID bookId);

    List<WaitList> findByStatusAndStartDateBefore(WaitListStatus status, LocalDateTime date);

    @Query(
            value = """
        SELECT * FROM wait_list 
        WHERE book_id = :bookId 
        AND status = :status 
        ORDER BY start_date ASC
        LIMIT 1
    """,
            nativeQuery = true
    )
    Optional<WaitList> findTopByBookIdAndStatusOrderByStartDateAsc(
        @Param("bookId") UUID bookId,
        @Param("status") String status
    );

    @Query(
            value =
        """
            DELETE FROM wait_list wl
            WHERE wl.book_copy_id = :bookCopyId
        """,
        nativeQuery = true
    )
    @Modifying
    void deleteByBookCopyId(@Param("bookCopyId") UUID bookCopyId);

}
