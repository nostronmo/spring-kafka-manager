package com.nostronmo.manage.instancer.history.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nostronmo.manage.instancer.history.dto.HistoryPageResponse;
import com.nostronmo.manage.instancer.history.dto.HistoryResponse;
import com.nostronmo.manage.instancer.history.model.HistoryStatus;
import com.nostronmo.manage.instancer.history.repository.HistorySortOption;
import com.nostronmo.manage.instancer.history.service.HistoryService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/history")
public class HistoryController {

	private final HistoryService service;

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public HistoryResponse findHistoryById(@PathVariable(value = "id") UUID query) {
		return service.findHistoryById(query);
	}

	@GetMapping(value = "/search")
	@ResponseStatus(HttpStatus.OK)
	public HistoryPageResponse searchHistoryByParams(
			@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
			@RequestParam(required = false) HistoryStatus status,
			@RequestParam(required = false, defaultValue = "LATEST") HistorySortOption sortOption) {
		return service.searchHistoryByParams(page, status, sortOption);
	}

}
