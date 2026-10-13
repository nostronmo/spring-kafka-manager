package com.nostronmo.manage.instancer.exception.quartz;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import com.nostronmo.manage.instancer.logic.quartz.QuartzFunctions;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ConditionalOnMissingBean(QuartzFunctions.class)
public class QuartzDisabledService implements QuartzFunctions {

	private void throwDisabled() {
		throw new QuartzUnavailableException("Quartz features are currently disabled by configuration.");
	}

	@Override
	public void stopJob(String jobId, String targetService) {
		throwDisabled();
	}

	@Override
	public void stopJob(String jobId) {
		throwDisabled();
	}

	@Override
	public void scheduleJob(String jobId, List<String> executorBeanName, String targetService, Duration duration,
			List<String> durationList) {
		throwDisabled();		
	}

	@Override
	public void scheduleJob(String jobId, List<String> executorBeanName, String targetService, Duration duration,
			List<String> durationList, ZonedDateTime lastRun) {
		throwDisabled();		
	}

}
