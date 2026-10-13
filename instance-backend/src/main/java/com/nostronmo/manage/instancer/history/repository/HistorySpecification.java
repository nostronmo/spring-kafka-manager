package com.nostronmo.manage.instancer.history.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nostronmo.manage.instancer.history.model.History;
import com.nostronmo.manage.instancer.history.model.HistoryStatus;

import jakarta.persistence.criteria.Predicate;

public class HistorySpecification {

	private HistorySpecification() {
	}

	public static Specification<History> findByCriteria(HistoryStatus status) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (status != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), status));
			}

			if (predicates.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
