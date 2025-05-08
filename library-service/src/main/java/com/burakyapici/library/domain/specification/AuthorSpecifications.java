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

            if (criteria.firstname() != null && !criteria.firstname().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstname")),
                    "%" + criteria.firstname().toLowerCase() + "%"
                ));
            }

            if (criteria.lastname() != null && !criteria.lastname().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lastname")),
                    "%" + criteria.lastname().toLowerCase() + "%"
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