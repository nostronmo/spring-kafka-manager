package com.nostronmo.manage.instancer.history.dto;

import java.util.List;

public record HistoryPageResponse(
		
		List<HistoryResponse> content,
		Integer page,
		Integer size,
		Integer totalPages
		
		) {

}
