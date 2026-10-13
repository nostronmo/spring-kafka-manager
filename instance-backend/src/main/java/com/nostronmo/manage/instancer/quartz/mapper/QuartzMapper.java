package com.nostronmo.manage.instancer.quartz.mapper;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.nostronmo.manage.instancer.quartz.dto.QuartzPageResponse;
import com.nostronmo.manage.instancer.quartz.dto.QuartzRequest;
import com.nostronmo.manage.instancer.quartz.dto.QuartzResponse;
import com.nostronmo.manage.instancer.quartz.dto.QuartzUpdate;
import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuartzMapper {

	QuartzScheduler build(QuartzRequest request);

	QuartzResponse toResponse(QuartzScheduler quartz);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void update(@MappingTarget QuartzScheduler quartz, QuartzUpdate request);

	List<QuartzResponse> toResponseList(List<QuartzScheduler> quartz);

	default QuartzPageResponse toResponse(Page<QuartzScheduler> page) {
		if (page == null) {
			return null;
		}
		List<QuartzResponse> content = toResponseList(page.getContent());
		return new QuartzPageResponse(content, page.getNumber(), page.getSize(), page.getTotalPages());
	}

	default Duration mapStringToDuration(String seconds) {
		if (seconds == null || seconds.isBlank()) {
			return null;
		}
		return Duration.ofSeconds(Long.parseLong(seconds));
	}

	default String mapDurationToString(Duration duration) {
		if (duration == null) {
			return null;
		}
		return String.valueOf(duration.getSeconds());
	}

	default String mapOptionalToString(Optional<String> optional) {
		return (optional != null) ? optional.orElse(null) : null;
	}

}
