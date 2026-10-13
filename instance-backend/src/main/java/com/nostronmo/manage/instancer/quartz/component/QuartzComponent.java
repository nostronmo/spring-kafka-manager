package com.nostronmo.manage.instancer.quartz.component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.exception.sql.DataViolationException;
import com.nostronmo.manage.instancer.exception.sql.NotFoundException;
import com.nostronmo.manage.instancer.global.ContextProvider;
import com.nostronmo.manage.instancer.logic.quartz.jobs.ScheduledTaskExecutor;
import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;
import com.nostronmo.manage.instancer.quartz.repository.QuartzRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzComponent {

	private final Map<String, ScheduledTaskExecutor> executorBeans;

	private final QuartzRepository quartzRepository;

	public QuartzScheduler findById(UUID query) {
		return quartzRepository.findById(query).orElseThrow(
				() -> new NotFoundException("Quartz Scheduled by id: %s, was not found.".formatted(query)));
	}

	public List<String> getAllExecutorBeanNames() {
		return new ArrayList<>(executorBeans.keySet());
	}

	public Map<String, String> getActiveContextsWithTopics() {
		Map<String, ScheduledTaskExecutor> beans = ContextProvider.getBeansOfType(ScheduledTaskExecutor.class);
		Map<String, String> result = new HashMap<>();

		beans.forEach((beanName, executor) -> result.put(beanName,
				executor.getTopic() != null ? executor.getTopic() : "unknown"));
		return result;
	}

	public String resolveExecutorBeanName(String identifier) {
		if (identifier == null || identifier.isBlank() || "all".equalsIgnoreCase(identifier)) {
			return "all";
		}

		Map<String, ScheduledTaskExecutor> beans = ContextProvider.getBeansOfType(ScheduledTaskExecutor.class);

		for (String beanName : beans.keySet()) {
			if (beanName.equalsIgnoreCase(identifier)) {
				return beanName;
			}
		}

		for (Map.Entry<String, ScheduledTaskExecutor> entry : beans.entrySet()) {
			ScheduledTaskExecutor executor = entry.getValue();
			if (executor.getTopic() != null && executor.getTopic().equalsIgnoreCase(identifier)) {
				return entry.getKey();
			}
		}

		throw new DataViolationException("No executor bean or topic found matching: " + identifier);
	}

	public void validateTimeWindowOverlap(UUID currentJobId, String targetService, List<Duration> requestedDurations) {
		List<QuartzScheduler> activeSchedules = quartzRepository.findByTargetServiceAndActiveTrue(targetService);

		for (QuartzScheduler active : activeSchedules) {
			if (currentJobId != null && active.getId().equals(currentJobId)) {
				continue;
			}

			if (active.getTime() == null || active.getTime().isEmpty()) {
				continue;
			}

			for (Duration existingDuration : active.getTime()) {
				for (Duration requestedDuration : requestedDurations) {
					if (existingDuration.equals(requestedDuration)) {
						throw new DataViolationException(
								"Target service '%s' already has an active schedule running at interval %s (Schedule: '%s')."
										.formatted(targetService, requestedDuration, active.getName()));
					}
				}
			}
		}
	}

}
