package com.burakyapici.library.domain.specification;

import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.domain.model.BookCopy;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookCopySpecifications {
    public static Specification<BookCopy> findByCriteria(BookCopySearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.barcode() != null && !criteria.barcode().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("barcode")),
                    "%" + criteria.barcode().toLowerCase() + "%"
                ));
            }

            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("status"),
                    criteria.status()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}