package com.burakyapici.library.domain.specification;

import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.api.dto.request.BookSearchCriteria;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.util.ArrayList;
import java.util.List;

public class BookSpecifications {
    public static Specification<Book> findByCriteria(BookSearchCriteria criteria) {
        return (Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.title() != null && !criteria.title().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.title().toLowerCase() + "%"));
            }

            if (criteria.isbn() != null && !criteria.isbn().isEmpty()) {
                predicates.add(cb.equal(root.get("isbn"), criteria.isbn()));
            }

            if (criteria.bookStatus() != null) {
                predicates.add(cb.equal(root.get("bookStatus"), criteria.bookStatus()));
            }

            if (criteria.page() != null) {
                predicates.add(cb.equal(root.get("page"), criteria.page()));
            }

            if (criteria.publicationDate() != null) {
                predicates.add(cb.equal(root.get("publicationDate"), criteria.publicationDate()));
            }

            if (criteria.genreId() != null) {
                Join<Book, Genre> genreJoin = root.joinSet("genres", JoinType.INNER);
                predicates.add(cb.equal(genreJoin.get("id"), criteria.genreId()));
            }

            if (criteria.authorId() != null) {
                Join<Book, Author> authorJoin = root.joinSet("authors", JoinType.INNER);
                predicates.add(cb.equal(authorJoin.get("id"), criteria.authorId()));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}