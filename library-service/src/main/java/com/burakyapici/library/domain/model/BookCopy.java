package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "book_copy")
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy extends BaseModel {
    @NotBlank
    @Size(max = 50)
    @Column(name = "barcode", nullable = false, unique = true, length = 50)
    private String barcode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", length = 20, nullable = false)
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Book.class)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @OneToMany(mappedBy = "bookCopy", fetch = FetchType.LAZY)
    private Set<Borrowing> borrowings = new HashSet<>();
    
    @OneToMany(mappedBy = "reservedBookCopy", fetch = FetchType.LAZY)
    private Set<WaitList> waitLists = new HashSet<>();
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public BookCopyStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public Set<Borrowing> getBorrowings() {
        return borrowings;
    }
    
    public void setBorrowings(Set<Borrowing> borrowings) {
        this.borrowings = borrowings;
    }
    
    public Set<WaitList> getWaitLists() {
        return waitLists;
    }
    
    public void setWaitLists(Set<WaitList> waitLists) {
        this.waitLists = waitLists;
    }
    
    @Override
    public UUID getId() {
        return super.getId();
    }
}
