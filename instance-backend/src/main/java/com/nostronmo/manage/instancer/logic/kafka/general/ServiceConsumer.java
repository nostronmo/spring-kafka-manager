package com.nostronmo.manage.instancer.logic.kafka.general;

import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nostronmo.manage.instancer.exception.kafka.KafkaException;
import com.nostronmo.manage.instancer.history.component.HistoryComponent;
import com.nostronmo.manage.instancer.logic.kafka.dto.ServiceData;
import com.nostronmo.manage.instancer.python.mapper.PythonMapper;
import com.nostronmo.manage.instancer.python.repository.PythonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
public class ServiceConsumer {

	private final PythonRepository webscrapingRepository;
	private final PythonMapper webscrapingMapper;
	private final HistoryComponent historyComponent;

	@Transactional
	@KafkaListener(topics = "${kafka.topics.services.status}", containerFactory = "serviceDataKafkaListenerContainerFactory")
	public void recieveScraperData(ServiceData consumer) {
		try {
			var webscraping = webscrapingMapper.build(consumer);
			
			Map<String, String> statusMap = Map.of(
	                "Service: ", Objects.toString(consumer.service(), "UNKNOWN"),
	                "Service_State:", Objects.toString(consumer.serviceState(), "UNKNOWN"),
	                "Timer: ", Objects.toString(consumer.time(), "0")
	            );
			historyComponent.historyServiceStatus(statusMap);
			webscrapingRepository.save(webscraping);
		} catch (KafkaException e) {
			log.error("", e.getCause());
		}
	}

}
