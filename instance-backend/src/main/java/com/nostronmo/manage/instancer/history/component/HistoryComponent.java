package com.nostronmo.manage.instancer.history.component;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.exception.sql.NotFoundException;
import com.nostronmo.manage.instancer.history.model.History;
import com.nostronmo.manage.instancer.history.model.HistoryStatus;
import com.nostronmo.manage.instancer.history.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HistoryComponent {

	private final HistoryRepository repository;

	public History findHistoryById(UUID query) {
		return repository.findById(query).orElseThrow(
				() -> new NotFoundException("N�o foi possivel encontrar o historico com id: %s".formatted(query)));
	}

	public void historyServiceScheduledTimer() {
		historyServiceScheduledTimer(null);
	}

	public void historyServiceStatus() {
		historyServiceStatus(null);
	}
	
	public void historyErrorStatus() {
		historyErrorStatus(null);
	}

	public void historyServiceScheduledTimer(Map<String, String> customInformation) {
		var history = History.builder()
				.status(HistoryStatus.SERVICE_SCHEDULED_TIMER)
				.customInformation(customInformation)
				.build();
		repository.save(history);
	}

	public void historyServiceStatus(Map<String, String> customInformation) {
		var history = History.builder()
				.status(HistoryStatus.SERVICE_STATUS)
				.customInformation(customInformation)
				.build();
		repository.save(history);
	}
	
	public void historyErrorStatus(Map<String, String> customInformation) {
		var history = History.builder()
				.status(HistoryStatus.ERROR)
				.customInformation(customInformation)
				.build();
		repository.save(history);
	}
	
	public void deleteHistoryById(UUID query) {
		var history = findHistoryById(query);
		repository.delete(history);
	}


}
