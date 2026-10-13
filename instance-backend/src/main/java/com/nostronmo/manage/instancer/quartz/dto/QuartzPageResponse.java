package com.nostronmo.manage.instancer.quartz.dto;

import java.util.List;

public record QuartzPageResponse(
		
		List<QuartzResponse> content,
		Integer page,
		Integer size,
		Integer totalPages
		
		) {

}
