package com.nostronmo.manage.instancer.history.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.nostronmo.manage.instancer.history.dto.HistoryPageResponse;
import com.nostronmo.manage.instancer.history.dto.HistoryResponse;
import com.nostronmo.manage.instancer.history.model.History;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryMapper {

	HistoryResponse toResponse(History history);

	List<HistoryResponse> toResponseList(List<History> history);

	default HistoryPageResponse toResponse(Page<History> page) {
		if (page == null) {
			return null;
		}
		List<HistoryResponse> content = toResponseList(page.getContent());
		return new HistoryPageResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());
	}
	
	
}
