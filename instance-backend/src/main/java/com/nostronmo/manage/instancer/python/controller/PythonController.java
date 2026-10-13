package com.nostronmo.manage.instancer.python.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nostronmo.manage.instancer.python.dto.PythonPageResponse;
import com.nostronmo.manage.instancer.python.dto.PythonResponse;
import com.nostronmo.manage.instancer.python.repository.PythonSortOption;
import com.nostronmo.manage.instancer.python.service.PythonService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/python")
public class PythonController {

	private final PythonService service;

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PythonResponse findPythonById(@PathVariable(value = "id") UUID query) {
		return service.findPythonById(query);
	}

	@GetMapping(value = "/search")
	@ResponseStatus(HttpStatus.OK)
	public PythonPageResponse searchPythonByQuery(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
			@RequestParam(required = false, value = "service") String serviceName,
			@RequestParam(required = false, value = "state") String serviceState,
			@RequestParam(required = false) String uptime,
			@RequestParam(required = false, defaultValue = "LATEST", value = "sort") PythonSortOption sortOption) {
		return service.searchPythonByQuery(page, serviceName, serviceState, uptime, sortOption);
	}

}
