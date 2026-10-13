package com.nostronmo.manage.instancer.quartz.dto;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record QuartzResponse(
		
		UUID id,
		String name,
		String kafkaTopic,
		String targetService,
		boolean active,
		ZonedDateTime lastRun,
		ZonedDateTime createdAt,
		List<Duration> time
		
		) {

}
