package com.burakyapici.library.domain.model;

import com.burakyapici.library.domain.enums.BookStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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

    @NaturalId
    @NotBlank
    @Size(min = 10, max = 13)
    @Column(name = "isbn", unique = true, nullable = false, length = 13)
    private String isbn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private BookStatus bookStatus;

    @Column(name = "page")
    private int page;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Builder.Default
    @ManyToMany(mappedBy = "books", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private final Set<Author> author = new HashSet<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_genre",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private final Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = WaitList.class)
    private final Set<WaitList> waitList = new HashSet<>();

    @OneToMany(
        mappedBy = "book",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        targetEntity = BookCopy.class
    )
    @Builder.Default
    private final Set<BookCopy> copies = new HashSet<>();
}
