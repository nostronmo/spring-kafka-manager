package com.nostronmo.manage.instancer.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.logic.quartz.QuartzFunctions;
import com.nostronmo.manage.instancer.quartz.component.QuartzComponent;
import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;
import com.nostronmo.manage.instancer.quartz.repository.QuartzRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzStart {

	private final QuartzRepository quartzRepository;
	private final QuartzFunctions quartzFunctions;
	private final QuartzComponent quartzComponent;
	
	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("Initializing active Quartz schedules from database...");

		List<QuartzScheduler> activeSchedules = quartzRepository.findByActiveTrue();

		if (activeSchedules.isEmpty()) {
			log.info("No active Quartz schedules found to start.");
			return;
		}
		
		for (QuartzScheduler quartz : activeSchedules) {
			try {
				String topic = quartz.getKafkaTopic();
				List<String> executorBeanNames = (topic != null && !topic.isBlank())
						? List.of(quartzComponent.resolveExecutorBeanName(topic))
						: quartzComponent.getAllExecutorBeanNames();

				String target = quartz.getTargetService();
				Duration firstDuration = quartz.getTime().get(0);
				List<String> cleanTimes = quartz.getTime().stream().map(Duration::toString).toList();
				String jobId = quartz.getId().toString();

				quartzFunctions.scheduleJob(jobId, executorBeanNames, target, firstDuration, cleanTimes, quartz.getLastRun());
				
				log.info("Restored Quartz job ID: {} for target service: {}", quartz.getId(), target);
			} catch (Exception e) {
				log.error("Failed to start Quartz job ID {}: {}", quartz.getId(), e.getMessage(), e.getCause());
			}
		}
	}
	
}
