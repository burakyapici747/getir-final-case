package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private UUID barcode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", length = 20, nullable = false)
    private BookCopyStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Book.class)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @OneToMany(mappedBy = "bookCopy", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Borrowing.class)
    private List<Borrowing> borrowingHistory = new ArrayList<>();
}
