package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(name = "borrowing")
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = BookCopy.class)
    @JoinColumn(name = "book_copy_id", referencedColumnName = "id")
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
    @JoinColumn(name = "processed_by_staff_id", referencedColumnName = "id")
    private User processedByStaff;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date", nullable = true)
    private LocalDateTime returnDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, targetEntity = User.class)
    @JoinColumn(name = "returned_by_staff_id", referencedColumnName = "id")
    private User returnedByStaff;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BorrowStatus status;
}
