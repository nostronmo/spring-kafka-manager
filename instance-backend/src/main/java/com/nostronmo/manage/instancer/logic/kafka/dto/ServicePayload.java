package com.nostronmo.manage.instancer.logic.kafka.dto;

import java.util.List;

public record ServicePayload(
		
		List<String> timer,
		String target
		
		) {

}
