package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@Builder
@Entity
@Table(name = "borrowing")
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Book.class)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private OffsetDateTime borrowDate;

    @Column(nullable = false)
    private OffsetDateTime dueDate;

    private OffsetDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BorrowStatus status;
}
