package com.nostronmo.manage.instancer.logic.kafka.config;

import java.time.Duration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.util.unit.DataSize;

@Lazy(false)
@Configuration
public class KafkaTopicConfig {

	@Value("${kafka.config.global.size}")
	private DataSize globalTopicSize; 
	
	@Value("${kafka.config.global.retention-time}")
	private Duration globalTopicRetentionTime;
	
	// PRODUCER

	@Value("${kafka.topics.services.timer}")
	private String servicesTimer;

	// CONSUMER

	@Value("${kafka.topics.services.status}")
	private String servicesStatus;

	@Bean
	NewTopic servicesTimerTopic() {
		return TopicBuilder.name(servicesTimer)
				.partitions(4)
				.replicas(1)
				.config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(globalTopicRetentionTime.toMillis()))
				.config(TopicConfig.RETENTION_BYTES_CONFIG, String.valueOf(globalTopicSize.toBytes()))
				.build();
	}

	@Bean
	NewTopic servicesStatusTopic() {
		return TopicBuilder.name(servicesStatus)
				.partitions(4)
				.replicas(1)
				.config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(globalTopicRetentionTime.toMillis()))
				.config(TopicConfig.RETENTION_BYTES_CONFIG, String.valueOf(globalTopicSize.toBytes()))
				.build();
	}

}