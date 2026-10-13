package com.nostronmo.manage.instancer.history.dto;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nostronmo.manage.instancer.history.model.HistoryStatus;

public record HistoryResponse(
		
		UUID id,
		
		HistoryStatus status,
		
		@JsonAlias({ "history_" })
		Map<String, String> customInformation,
		
		ZonedDateTime createdAt
		
		) {

}
