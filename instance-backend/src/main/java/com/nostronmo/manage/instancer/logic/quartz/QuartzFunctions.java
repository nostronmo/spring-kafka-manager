package com.nostronmo.manage.instancer.logic.quartz;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public interface QuartzFunctions {

	void scheduleJob(String jobId, List<String> executorBeanName, String targetService, Duration duration,
			List<String> durationList);

	void scheduleJob(String jobId, List<String> executorBeanName, String targetService, Duration duration,
			List<String> durationList, ZonedDateTime lastRun);

	void stopJob(String jobId, String targetService);

	void stopJob(String jobId);

	/*
	 * start now the schedule job and make their duration for example call it again
	 * on 6 hours stop schedule, or how to give name to the schedule to call it
	 * later to stop it.
	 * 
	 * void scheduleJob(Duration duration, String executorBeanName, String
	 * targetService, List<String> rawDurationList); // start job with a delay
	 * 
	 * void stopJob(String jobId);
	 * 
	 * void scheduleJob(String jobId, Runnable scheduleJob, Duration duration);
	 * 
	 * void scheduleJob(Runnable scheduleJob, Duration duration);
	 * 
	 * void scheduleJob(String jobId, Runnable scheduleJob); // one time job instant
	 * 
	 * void scheduleJob(String jobId, Runnable scheduleJob, Instant instant); // one
	 * job at a time
	 * 
	 * void scheduleJob(String jobId, Runnable scheduleJob, Duration duration); //
	 * repeating job every time
	 * 
	 * void scheduleJob(String jobId, Runnable sheduleJob, List<Duration> duration);
	 * 
	 * void scheduleJob(runnable scheduleJob, Instant instant, Duration duration);
	 * // start job with a delay
	 * 
	 * void stopJob(String jobId);
	 * 
	 */
}
