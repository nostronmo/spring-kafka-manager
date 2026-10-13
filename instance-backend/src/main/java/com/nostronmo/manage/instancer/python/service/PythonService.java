package com.nostronmo.manage.instancer.python.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nostronmo.manage.instancer.python.component.PythonComponent;
import com.nostronmo.manage.instancer.python.dto.PythonPageResponse;
import com.nostronmo.manage.instancer.python.dto.PythonResponse;
import com.nostronmo.manage.instancer.python.mapper.PythonMapper;
import com.nostronmo.manage.instancer.python.model.PythonServices;
import com.nostronmo.manage.instancer.python.repository.PythonRepository;
import com.nostronmo.manage.instancer.python.repository.PythonSortOption;
import com.nostronmo.manage.instancer.python.repository.PythonSortParameter;
import com.nostronmo.manage.instancer.python.repository.PythonSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PythonService {

	@Value("${application.page.size:20}")
	private int PAGE_SIZE;

	private final PythonRepository pythonRepository;
	private final PythonMapper pythonMapper;
	private final PythonComponent pythonComponent;
	private final PythonSortParameter pythonSort;

	@Transactional(readOnly = true)
	public PythonResponse findPythonById(UUID query) {
		var webscraping = pythonComponent.findById(query);
		return pythonMapper.toResponse(webscraping);
	}

	@Transactional(readOnly = true)
	public PythonPageResponse searchPythonByQuery(int page, String serviceName, String scrapingStatus, String uptime,
			PythonSortOption sortOption) {
		var sort = pythonSort.selectSort(sortOption);
		Specification<PythonServices> spec = PythonSpecification.findByCriteria(serviceName, scrapingStatus, uptime);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
		Page<PythonServices> python = pythonRepository.findAll(spec, pageable);
		return pythonMapper.toResponse(python);
	}

}
