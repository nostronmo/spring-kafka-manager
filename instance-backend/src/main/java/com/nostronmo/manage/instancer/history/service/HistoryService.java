package com.nostronmo.manage.instancer.history.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nostronmo.manage.instancer.history.component.HistoryComponent;
import com.nostronmo.manage.instancer.history.dto.HistoryPageResponse;
import com.nostronmo.manage.instancer.history.dto.HistoryResponse;
import com.nostronmo.manage.instancer.history.mapper.HistoryMapper;
import com.nostronmo.manage.instancer.history.model.History;
import com.nostronmo.manage.instancer.history.model.HistoryStatus;
import com.nostronmo.manage.instancer.history.repository.HistoryRepository;
import com.nostronmo.manage.instancer.history.repository.HistorySortOption;
import com.nostronmo.manage.instancer.history.repository.HistorySortParameter;
import com.nostronmo.manage.instancer.history.repository.HistorySpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	@Value("${application.page.size:20}")
	private int PAGE_SIZE;

	private final HistoryRepository repository;
	private final HistoryMapper mapper;

	private final HistoryComponent component;
	private final HistorySortParameter historySort;

	@Transactional(readOnly = true)
	public HistoryResponse findHistoryById(UUID query) {
		var history = component.findHistoryById(query);
		return mapper.toResponse(history);
	}

	@Transactional(readOnly = true)
	public HistoryPageResponse searchHistoryByParams(int page, HistoryStatus status, HistorySortOption sortOption) {
		var sort = historySort.selectSort(sortOption);
		Specification<History> spec = HistorySpecification.findByCriteria(status);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
		Page<History> history = repository.findAll(spec, pageable);
		return mapper.toResponse(history);
	}

}
