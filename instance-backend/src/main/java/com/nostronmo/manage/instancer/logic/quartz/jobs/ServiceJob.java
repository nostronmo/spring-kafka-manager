package com.nostronmo.manage.instancer.logic.quartz.jobs;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.logic.kafka.dto.ServicePayload;
import com.nostronmo.manage.instancer.logic.kafka.general.ServiceProducer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("kafkaScrapingExecutor")
@RequiredArgsConstructor
public class ServiceJob implements ScheduledTaskExecutor, BeanNameAware {

	@Getter
	private String beanName;
	private final ServiceProducer serviceProducer;

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public String getTopic() {
		return serviceProducer.getServicesTimer();
	}

	@Override
	public void execute(String jobId, String targetService, List<String> duration) {
		ServicePayload payload = new ServicePayload(duration, targetService);
		serviceProducer.sendStartQueue(UUID.fromString(jobId), payload);
	}

	@Override
	public void stop(String jobId, String targetService) {
		ServicePayload payload = new ServicePayload(null, targetService);
		serviceProducer.sendStopQueue(UUID.fromString(jobId), payload);
	}

}
