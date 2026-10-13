package com.nostronmo.manage.instancer.quartz.repository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;

@Component
public class QuartzSortParameter {

	public Sort selectSort(QuartzSortOption sort) {
		if (sort == null) {
			return Sort.by(QuartzScheduler::getCreatedAt).ascending();
		}
		return switch (sort) {
		case LATEST -> Sort.by(QuartzScheduler::getCreatedAt).descending();
		case OLDEST -> Sort.by(QuartzScheduler::getCreatedAt).ascending();
		};
	}

}
