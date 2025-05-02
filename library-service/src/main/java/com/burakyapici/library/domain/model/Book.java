package com.burakyapici.library.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.burakyapici.library.domain.enums.AvailabilityStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class Book extends BaseModel {
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String author;

    @NaturalId
    @NotBlank
    @Size(min = 10, max = 13)
    @Column(name = "isbn", unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Size(max = 100)
    @Column(length = 100)
    private String genre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", length = 20, nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Min(0)
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @Min(0)
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY, targetEntity = BookDetail.class)
    private BookDetail bookDetail;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Borrowing.class)
    private Set<Borrowing> borrowingList = new HashSet<>();

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            targetEntity = Reservation.class,
            orphanRemoval = true
    )
    private Set<Reservation> reservationList = new HashSet<>();
}
