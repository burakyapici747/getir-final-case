package com.burakyapici.library.domain.specification;

import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.domain.model.Author;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AuthorSpecifications {
    public static Specification<Author> findByCriteria(AuthorSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.firstName() != null && !criteria.firstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstName")),
                    "%" + criteria.firstName().toLowerCase() + "%"
                ));
            }

            if (criteria.lastName() != null && !criteria.lastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lastName")),
                    "%" + criteria.lastName().toLowerCase() + "%"
                ));
            }

            if (criteria.dateOfBirth() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("dateOfBirth"),
                    criteria.dateOfBirth()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}