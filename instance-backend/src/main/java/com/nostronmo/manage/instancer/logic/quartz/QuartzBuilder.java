package com.nostronmo.manage.instancer.logic.quartz;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.logic.quartz.jobs.DelegatingJob;
import com.nostronmo.manage.instancer.logic.quartz.jobs.ScheduledTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzBuilder implements QuartzFunctions {

	private static final int STAGGER_STEP_SECONDS = 10;

	@Value("${quartz.job.services.name}")
	private String quartzJob;

	@Value("${kafka.topics.services.timer}")
	private String servicesTimer;

	private final ApplicationContext applicationContext;
	private final Scheduler scheduler;

	@Override
	public void scheduleJob(String jobId, List<String> executorBeanName, String targetService, Duration duration,
			List<String> durationList) {
		scheduleJobInternal(executorBeanName, jobId, duration, targetService, durationList, null);
	}

	@Override
	public void scheduleJob(String jobId, List<String> executorBeanName, String targetService, Duration duration,
			List<String> durationList, ZonedDateTime lastRun) {
		scheduleJobInternal(executorBeanName, jobId, duration, targetService, durationList, lastRun);
	}

	@Override
	public void stopJob(String jobId, String targetService) {
		stopJobInternal(jobId, targetService);
	}

	@Override
	public void stopJob(String jobId) {
		try {
			String[] allExecutors = applicationContext.getBeanNamesForType(ScheduledTaskExecutor.class);

			for (String beanName : allExecutors) {
				JobKey keyWithSuffix = new JobKey(jobId + "#" + beanName, quartzJob);
				if (scheduler.checkExists(keyWithSuffix)) {
					scheduler.deleteJob(keyWithSuffix);
					log.info("Stopped Quartz Job ID: {}", keyWithSuffix.getName());
				}
			}

			JobKey baseKey = new JobKey(jobId, quartzJob);
			if (scheduler.checkExists(baseKey)) {
				scheduler.deleteJob(baseKey);
				log.info("Stopped Quartz Job ID: {}", jobId);
			}
		} catch (SchedulerException e) {
			log.error("Failed to stop Quartz Job ID: {}", jobId, e);
		}
	}

	private void scheduleJobInternal(List<String> executorBeanName, String jobId, Duration duration,
			String targetService, List<String> durationList, ZonedDateTime lastRun) {

		stopJob(jobId);

		for (String executorBeanNamed : executorBeanName) {
			try {
				String uniqueJobName = (executorBeanName.size() > 1) ? jobId + "#" + executorBeanNamed : jobId;
				JobKey jobKey = new JobKey(uniqueJobName, quartzJob);

				if (scheduler.checkExists(jobKey)) {
					scheduler.deleteJob(jobKey);
				}

				JobDetail jobDetail = JobBuilder.newJob(DelegatingJob.class)
						.withIdentity(jobKey)
						.usingJobData("jobId", jobId)
						.usingJobData("targetService", targetService)
						.usingJobData("executorBeanName", executorBeanNamed)
						.build();
				jobDetail.getJobDataMap().put("duration", durationList);

				int intervalInSeconds = (int) Math.max(1, duration.getSeconds());
				Instant startAt = calculateStaggeredStartAt(jobId, duration, lastRun);

				Trigger trigger = TriggerBuilder.newTrigger()
						.withIdentity(uniqueJobName + "-trigger", quartzJob)
						.startAt(Date.from(startAt))
						.withSchedule(SimpleScheduleBuilder.simpleSchedule()
								.withIntervalInSeconds(intervalInSeconds)
								.repeatForever()
								.withMisfireHandlingInstructionNextWithExistingCount())
						.build();


				scheduler.scheduleJob(jobDetail, trigger);
				log.info("Successfully scheduled Quartz Job ID: {}", jobId);

			} catch (SchedulerException e) {
				log.error("Failed to schedule Quartz Job", e.getCause());
			}
		}
	}

	private Instant calculateStaggeredStartAt(String jobId, Duration duration, ZonedDateTime lastRun) {
		Instant now = Instant.now();

		if (lastRun != null) {
			Instant nextExpectedRun = lastRun.toInstant().plus(duration);
			if (nextExpectedRun.isAfter(now)) {
				return nextExpectedRun;
			}
		}

		try {
			int activeTriggers = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup()).size();
			return now.plusSeconds(((activeTriggers * STAGGER_STEP_SECONDS) + (Math.abs(jobId.hashCode() % 12) * 10))
					% Math.max(1, duration.getSeconds()));

		} catch (SchedulerException e) {
			log.warn("Could not query active triggers for staggering. Defaulting to minimal offset.", e.getCause());
			return now.plusSeconds(5);
		}
	}

	private void stopJobInternal(String jobId, String targetService) {
		String[] allExecutors = applicationContext.getBeanNamesForType(ScheduledTaskExecutor.class);
		for (String beanName : allExecutors) {
			try {
				ScheduledTaskExecutor executor = applicationContext.getBean(beanName, ScheduledTaskExecutor.class);
				executor.stop(jobId, targetService);
			} catch (Exception e) {
				log.error("Failed to execute stop command on executor: {}", beanName, e);
			}
		}
		stopJob(jobId);
	}

}
