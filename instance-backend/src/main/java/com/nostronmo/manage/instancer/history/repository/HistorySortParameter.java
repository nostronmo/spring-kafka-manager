package com.nostronmo.manage.instancer.history.repository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.history.model.History;

@Component
public class HistorySortParameter {

	public Sort selectSort(HistorySortOption sort) {
		if (sort == null) {
			return Sort.by(History::getCreatedAt).ascending();
		}
		return switch (sort) {
		case LATEST -> Sort.by(History::getCreatedAt).descending();
		case OLDEST -> Sort.by(History::getCreatedAt).ascending();
		};
	}

}
