package com.nostronmo.manage.instancer.logic.kafka.general;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.exception.kafka.KafkaException;
import com.nostronmo.manage.instancer.history.component.HistoryComponent;
import com.nostronmo.manage.instancer.logic.kafka.dto.ServicePayload;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Lazy(false)
public class ServiceProducer {

	@Value("${kafka.topics.services.timer}")
	@Getter
	private String servicesTimer;

	private final KafkaTemplate<String, ServicePayload> kafkaTemplate;

	private final HistoryComponent historyComponent;

	public ServiceProducer(@Qualifier("serviceKafkaPayload") KafkaTemplate<String, ServicePayload> kafkaTemplate,
			HistoryComponent historyComponent) {
		this.kafkaTemplate = kafkaTemplate;
		this.historyComponent = historyComponent;
	}

	public void sendStartQueue(UUID query, ServicePayload payload) {
		try {
			log.info("");
			kafkaTemplate.send(servicesTimer, String.valueOf(query), payload).whenComplete((result, exception) -> {
				if (exception != null) {
					log.error("", exception.getMessage());
				}
			});
			String timerString = payload.timer() != null ? payload.timer().toString() : "[]";
			historyComponent.historyServiceScheduledTimer(Collections.singletonMap("Timer: ", timerString));
		} catch (KafkaException e) {
			log.error("", e.getCause());
		}
	}

	public void sendStopQueue(UUID query, ServicePayload payload) {
		try {
			log.info("");
			kafkaTemplate.send(servicesTimer, String.valueOf(query), new ServicePayload(List.of(), payload.target()))
					.whenComplete((result, exception) -> {
						if (exception != null) {
							log.error("", exception.getMessage());
						}
					});
			String timerString = payload.timer() != null ? payload.timer().toString() : "[]";
			historyComponent.historyServiceScheduledTimer(Collections.singletonMap("Timer: ", timerString));
		} catch (KafkaException e) {
			log.error("", e.getCause());
		}
	}

}
