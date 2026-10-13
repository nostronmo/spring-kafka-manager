package com.nostronmo.manage.instancer.quartz.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nostronmo.manage.instancer.exception.sql.DataViolationException;
import com.nostronmo.manage.instancer.global.DurationListConverter;
import com.nostronmo.manage.instancer.logic.quartz.QuartzFunctions;
import com.nostronmo.manage.instancer.quartz.component.QuartzComponent;
import com.nostronmo.manage.instancer.quartz.dto.QuartzPageResponse;
import com.nostronmo.manage.instancer.quartz.dto.QuartzRequest;
import com.nostronmo.manage.instancer.quartz.dto.QuartzResponse;
import com.nostronmo.manage.instancer.quartz.dto.QuartzUpdate;
import com.nostronmo.manage.instancer.quartz.mapper.QuartzMapper;
import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;
import com.nostronmo.manage.instancer.quartz.repository.QuartzRepository;
import com.nostronmo.manage.instancer.quartz.repository.QuartzSortOption;
import com.nostronmo.manage.instancer.quartz.repository.QuartzSortParameter;
import com.nostronmo.manage.instancer.quartz.repository.QuartzSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzService {

    @Value("${application.page.size:20}")
    private int PAGE_SIZE;

    private final QuartzRepository quartzRepository;
    private final QuartzMapper quartzMapper;

    private final QuartzComponent quartzComponent;
    private final QuartzSortParameter quartzSort;

    private final QuartzFunctions quartzFunctions;

    @Transactional(readOnly = true)
    public Map<String, String> allActiveContexts() {
        return quartzComponent.getActiveContextsWithTopics();
    }

    @Transactional(readOnly = true)
    public QuartzResponse findQuartzById(UUID query) {
        var quartz = quartzComponent.findById(query);
        return quartzMapper.toResponse(quartz);
    }

    @Transactional(readOnly = true)
    public QuartzPageResponse searchQuartzByParams(
        int page,
        String kafkaTopic,
        String targetService,
        QuartzSortOption sortOption
    ) {
        var sort = quartzSort.selectSort(sortOption);
        Specification<QuartzScheduler> spec =
            QuartzSpecification.findByCriteria(kafkaTopic, targetService);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
        Page<QuartzScheduler> quartz = quartzRepository.findAll(spec, pageable);
        return quartzMapper.toResponse(quartz);
    }

    @Transactional
    public QuartzResponse toggleQuartzStatus(UUID query) {
        var quartz = quartzComponent.findById(query);
        quartz.setActive(!quartz.isActive());
        quartzRepository.save(quartz);
        return quartzMapper.toResponse(quartz);
    }

    @Transactional
    public QuartzResponse createSchedule(QuartzRequest request) {
        if (quartzRepository.existsByName(request.name())) {
            throw new DataViolationException(
                "A schedule with the name %s already exists.".formatted(
                    request.name()
                )
            );
        }

        String topic = request
            .kafkaTopic()
            .filter(t -> !t.isBlank())
            .map(String::toLowerCase)
            .orElse("all");

        List<String> executorBeanNames = "all".equalsIgnoreCase(topic)
            ? quartzComponent.getAllExecutorBeanNames()
            : List.of(quartzComponent.resolveExecutorBeanName(topic));

        String target = request
            .targetService()
            .filter(t -> !t.isBlank())
            .orElse("all")
            .toLowerCase();

        if (quartzRepository.existsByTargetService(target)) {
            throw new DataViolationException(
                "A schedule for target service '%s' already exists.".formatted(
                    target
                )
            );
        }

        List<String> validTimes = request
            .time()
            .stream()
            .filter(t -> t != null && !t.trim().isEmpty())
            .toList();
        if (validTimes.isEmpty()) {
            throw new DataViolationException(
                "Time list is required and must contain valid entries."
            );
        }

        DurationListConverter converter = new DurationListConverter();
        List<Duration> parsedDurations = validTimes
            .stream()
            .map(converter::parseDuration)
            .toList();
        quartzComponent.validateTimeWindowOverlap(
            null,
            target,
            parsedDurations
        );

        var quartz = quartzMapper.build(request);
        quartz.setTargetService(target);
        quartzRepository.save(quartz);

        String jobId = quartz.getId().toString();
        Duration firstDuration = parsedDurations.get(0);

        quartzFunctions.scheduleJob(
            jobId,
            executorBeanNames,
            target,
            firstDuration,
            request.time()
        );

        return quartzMapper.toResponse(quartz);
    }

    @Transactional
    public QuartzResponse updateSchedule(QuartzUpdate request) {
        var quartz = quartzComponent.findById(request.query());
        String jobId = quartz.getId().toString();
        String oldTarget = quartz.getTargetService();

        String newTarget = request
            .targetService()
            .filter(t -> !t.isBlank())
            .orElse("all")
            .toLowerCase();

        if (request.time() == null || request.time().isEmpty()) {
            throw new DataViolationException(
                "Time list is required and cannot be empty."
            );
        }

        List<Duration> parsedDurations = request
            .time()
            .stream()
            .map(new DurationListConverter()::parseDuration)
            .toList();
        quartzComponent.validateTimeWindowOverlap(
            quartz.getId(),
            newTarget,
            parsedDurations
        );

        quartzFunctions.stopJob(jobId, oldTarget);

        String topic = request
            .kafkaTopic()
            .filter(t -> !t.isBlank())
            .orElse(quartz.getKafkaTopic());

        if (topic == null || topic.isBlank()) {
            topic = "all";
        }

        List<String> executorBeanNames = "all".equalsIgnoreCase(topic)
            ? quartzComponent.getAllExecutorBeanNames()
            : List.of(quartzComponent.resolveExecutorBeanName(topic));

        quartzMapper.update(quartz, request);
        quartz.setKafkaTopic(topic);
        quartzRepository.save(quartz);

        Duration firstDuration = parsedDurations.get(0);

        quartzFunctions.scheduleJob(
            jobId,
            executorBeanNames,
            newTarget,
            firstDuration,
            request.time(),
            quartz.getLastRun()
        );

        return quartzMapper.toResponse(quartz);
    }

    @Transactional
    public void deleteQuartzById(UUID query) {
        var quartz = quartzComponent.findById(query);
        quartzFunctions.stopJob(
            quartz.getId().toString(),
            quartz.getTargetService()
        );
        quartzRepository.delete(quartz);
    }
}
