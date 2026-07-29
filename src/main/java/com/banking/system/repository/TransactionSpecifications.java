package com.banking.system.repository;

import com.banking.system.domain.Transaction;
import com.banking.system.domain.TransactionStatus;
import com.banking.system.domain.TransactionType;
import com.banking.system.web.dto.TransactionSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> withCriteria(TransactionSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getTransactionType() != null && !criteria.getTransactionType().isEmpty()) {
                try {
                    predicates.add(criteriaBuilder.equal(root.get("transactionType"), 
                            TransactionType.valueOf(criteria.getTransactionType().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    // skip invalid enum query
                }
            }

            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                try {
                    predicates.add(criteriaBuilder.equal(root.get("status"), 
                            TransactionStatus.valueOf(criteria.getStatus().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    // skip invalid enum query
                }
            }

            if (criteria.getMinAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), criteria.getMinAmount()));
            }

            if (criteria.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), criteria.getMaxAmount()));
            }

            if (criteria.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getStartDate()));
            }

            if (criteria.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), criteria.getEndDate()));
            }

            if (criteria.getAccountNumber() != null && !criteria.getAccountNumber().isEmpty()) {
                Predicate sourceMatch = criteriaBuilder.equal(root.get("sourceAccount").get("accountNumber"), criteria.getAccountNumber());
                Predicate targetMatch = criteriaBuilder.equal(root.get("targetAccount").get("accountNumber"), criteria.getAccountNumber());
                predicates.add(criteriaBuilder.or(sourceMatch, targetMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
