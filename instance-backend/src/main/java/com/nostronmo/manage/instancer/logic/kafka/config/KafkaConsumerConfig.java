package com.nostronmo.manage.instancer.logic.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.nostronmo.manage.instancer.logic.kafka.dto.ServiceData;

@Lazy(false)
@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	ConsumerFactory<String, ServiceData> serviceDataConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "service-monitor-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		StringDeserializer stringDeserializer = new StringDeserializer();
		ErrorHandlingDeserializer<String> keyDeserializer = new ErrorHandlingDeserializer<>(stringDeserializer);

		JacksonJsonDeserializer<ServiceData> jsonDeserializer = new JacksonJsonDeserializer<>(ServiceData.class);
		jsonDeserializer.addTrustedPackages("*");
		jsonDeserializer.setRemoveTypeHeaders(false);
		jsonDeserializer.setUseTypeMapperForKey(false);
		ErrorHandlingDeserializer<ServiceData> valueDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);

		return new DefaultKafkaConsumerFactory<>(props, keyDeserializer, valueDeserializer);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, ServiceData> serviceDataKafkaListenerContainerFactory(
			@Qualifier("serviceErrorHandler") CommonErrorHandler errorHandler) {
		ConcurrentKafkaListenerContainerFactory<String, ServiceData> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(serviceDataConsumerFactory());
		factory.setConcurrency(2);
		factory.getContainerProperties().setStopImmediate(true);
		return factory;
	}
}
