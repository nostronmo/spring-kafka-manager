package com.nostronmo.manage.instancer.python.dto;

import java.util.Map;

import com.nostronmo.manage.instancer.python.model.ServiceState;

public record PythonResponse(
		
		String name,
		
		String service,
		
		String serviceState,
		
		Map<String, ServiceState> checkpoints,
		
		String uptime,
		
		String status,
		
		String framework
		
		) {

}
