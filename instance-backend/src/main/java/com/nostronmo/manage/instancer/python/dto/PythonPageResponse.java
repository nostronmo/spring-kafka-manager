package com.nostronmo.manage.instancer.python.dto;

import java.util.List;

public record PythonPageResponse(
		
		List<PythonResponse> content,
		Integer page,
		Integer size,
		Integer totalPages
		
		) {

}
