package com.nostronmo.manage.instancer.logic.quartz.component;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nostronmo.manage.instancer.quartz.repository.QuartzRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzLogicComponent {

	private final QuartzRepository schedulerRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateLastRun(String jobId) {
		try {
			UUID id = UUID.fromString(jobId);
			schedulerRepository.findById(id).ifPresent(scheduler -> {
				scheduler.setLastRun(ZonedDateTime.now());
				schedulerRepository.saveAndFlush(scheduler);
			});
		} catch (IllegalArgumentException e) {
			log.error("Invalid UUID format for jobId: {}", jobId, e);
		}
	}
}
