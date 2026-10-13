package com.nostronmo.manage.instancer.python.repository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.python.model.PythonServices;

@Component
public class PythonSortParameter {

	public Sort selectSort(PythonSortOption sort) {
		if (sort == null) {
			return Sort.by(PythonServices::getCreatedAt).ascending();
		}
		return switch (sort) {
		case LATEST -> Sort.by(PythonServices::getCreatedAt).descending();
		case OLDEST -> Sort.by(PythonServices::getCreatedAt).ascending();
		};
	}

}
