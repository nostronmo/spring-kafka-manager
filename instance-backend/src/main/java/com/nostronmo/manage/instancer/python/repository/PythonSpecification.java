package com.nostronmo.manage.instancer.python.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nostronmo.manage.instancer.python.model.PythonServices;

import jakarta.persistence.criteria.Predicate;

public class PythonSpecification {

	private PythonSpecification() {
	}

	public static Specification<PythonServices> findByCriteria(String serviceName, String serviceState, String uptime) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (serviceName != null) {
				predicates.add(criteriaBuilder.equal(root.get("service"), serviceName));
			}

			if (serviceState != null) {
				predicates.add(criteriaBuilder.equal(root.get("serviceState"), serviceState));
			}

			if (uptime != null) {
				predicates.add(criteriaBuilder.equal(root.get("uptime"), uptime));
			}

			if (predicates.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
