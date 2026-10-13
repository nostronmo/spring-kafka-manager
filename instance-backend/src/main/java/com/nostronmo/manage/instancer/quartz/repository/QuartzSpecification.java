package com.nostronmo.manage.instancer.quartz.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;

import jakarta.persistence.criteria.Predicate;

public class QuartzSpecification {

	private QuartzSpecification() {
	}

	public static Specification<QuartzScheduler> findByCriteria(String kafkaTopic, String targetService) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (kafkaTopic != null) {
				predicates.add(criteriaBuilder.equal(root.get("kafka_topic"), kafkaTopic));
			}

			if (targetService != null) {
				predicates.add(criteriaBuilder.equal(root.get("target_service"), targetService));
			}

			if (predicates.isEmpty()) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
