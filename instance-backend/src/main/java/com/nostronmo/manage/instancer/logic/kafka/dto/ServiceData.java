package com.nostronmo.manage.instancer.logic.kafka.dto;

public record ServiceData(
		
		String name,
		String service,
		String serviceState,
		String checkpoints,
		String time,
		String status,
		String framework
		
		) {

}
