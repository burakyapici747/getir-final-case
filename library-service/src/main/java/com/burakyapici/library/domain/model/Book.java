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
@NamedEntityGraph(
    name = "Book.detail",
    attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("genres"),
        @NamedAttributeNode(value = "waitLists", subgraph = "waitLists-with-user"),
        @NamedAttributeNode("bookCopies")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "waitLists-with-user",
            attributeNodes = @NamedAttributeNode("user")
        )
    }
)
@NamedEntityGraph(
    name = "Book",
    attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("genres"),
    }
)
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_genre",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, targetEntity = WaitList.class)
    private Set<WaitList> waitLists = new HashSet<>();

    @OneToMany(
        mappedBy = "book",
        fetch = FetchType.LAZY,
        targetEntity = BookCopy.class
    )
    @Builder.Default
    private Set<BookCopy> bookCopies = new HashSet<>();
}
