package com.nostronmo.manage.instancer.logic.kafka.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaDequeueConsumer {

	// TODO: Make a dequeue to monitor the data that was lost

	@KafkaListener(topicPattern = ".*\\.DLT", groupId = "dlq-monitoring-group")
	public void processDlqMessage(String failedPayload, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage,
			@Header(KafkaHeaders.ORIGINAL_OFFSET) byte[] originalOffset) {
	}

	@Bean
	CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
		return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template),
				new FixedBackOff(Duration.ofSeconds(5).toMillis(), 3));
	}
}
