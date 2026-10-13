package com.nostronmo.manage.instancer.python.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.nostronmo.manage.instancer.logic.kafka.dto.ServiceData;
import com.nostronmo.manage.instancer.python.dto.PythonPageResponse;
import com.nostronmo.manage.instancer.python.dto.PythonResponse;
import com.nostronmo.manage.instancer.python.model.PythonServices;
import com.nostronmo.manage.instancer.python.model.ServiceState;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PythonMapper {

	@Mapping(target = "uptime", source = "time")
	@Mapping(target = "checkpoints", expression = "java(mapCheckpoints(consumer.checkpoints()))")
	PythonServices build(ServiceData consumer);

	PythonResponse toResponse(PythonServices webscraping);

	List<PythonResponse> toResponseList(List<PythonServices> webscraping);

	default PythonPageResponse toResponse(Page<PythonServices> page) {
		if (page == null) {
			return null;
		}
		List<PythonResponse> content = toResponseList(page.getContent());
		return new PythonPageResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());
	}

	default Map<String, ServiceState> mapCheckpoints(String checkpoints) {
		if (checkpoints == null) {
			return null;
		}
		ServiceState state = new ServiceState();
		state.setProgress(checkpoints);
		return Map.of("default", state);
	}

}
