package com.nostronmo.manage.instancer.quartz.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nostronmo.manage.instancer.quartz.dto.QuartzPageResponse;
import com.nostronmo.manage.instancer.quartz.dto.QuartzRequest;
import com.nostronmo.manage.instancer.quartz.dto.QuartzResponse;
import com.nostronmo.manage.instancer.quartz.dto.QuartzUpdate;
import com.nostronmo.manage.instancer.quartz.repository.QuartzSortOption;
import com.nostronmo.manage.instancer.quartz.service.QuartzService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/quartz")
public class QuartzController {

	private final QuartzService service;

	@GetMapping(value = "/contexts")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, String> findAllActiveContexts() {
		return service.allActiveContexts();
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public QuartzResponse findQuartzScheduleById(@PathVariable(value = "id") UUID query) {
		return service.findQuartzById(query);
	}

	@GetMapping(value = "/search")
	@ResponseStatus(HttpStatus.OK)
	public QuartzPageResponse searchQuartzByParams(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, value = "topic") String kafkaTopic,
			@RequestParam(required = false, value = "service") String targetService,
			@RequestParam(required = false, defaultValue = "LATEST", value = "sort") QuartzSortOption sortParams) {
		return service.searchQuartzByParams(page, kafkaTopic, targetService, sortParams);
	}

	@PutMapping(value = "/status/{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public QuartzResponse toggleQuartzStatus(@PathVariable(value = "id") UUID query) {
		return service.toggleQuartzStatus(query);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public QuartzResponse createQuartzSchedule(@RequestBody @Valid QuartzRequest request) {
		return service.createSchedule(request);
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public QuartzResponse updateQuartzSchedule(@RequestBody @Valid QuartzUpdate request) {
		return service.updateSchedule(request);
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuartzScheduleById(@PathVariable(value = "id") UUID query) {
		service.deleteQuartzById(query);
	}
}
