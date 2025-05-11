package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "borrowing")
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = BookCopy.class)
    @JoinColumn(name = "book_copy_id", referencedColumnName = "id")
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
    @JoinColumn(name = "borrowed_by_staff_id", referencedColumnName = "id")
    private User borrowedByStaff;

    @ManyToOne(fetch = FetchType.LAZY, optional = true, targetEntity = User.class)
    @JoinColumn(name = "returned_by_staff_id", referencedColumnName = "id")
    private User returnedByStaff;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date", nullable = true)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWED;
}
