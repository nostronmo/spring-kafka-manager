package com.nostronmo.manage.instancer.quartz.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import com.nostronmo.manage.instancer.global.Auditable;
import com.nostronmo.manage.instancer.global.DurationListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "quartz_scheduler")
public class QuartzScheduler extends Auditable {

	@Id
	@ToString.Include
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ToString.Include
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "kafka_topic", nullable = true)
	private String kafkaTopic;

	@Column(name = "target_service", nullable = false)
	private String targetService;

	@Convert(converter = DurationListConverter.class)
	@Column(name = "interval_duration", nullable = false)
	private List<Duration> time;

	@Builder.Default
	@ToString.Include
	@Column(name = "active", nullable = false)
	private boolean active = true;

	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	@Column(name = "last_run", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime lastRun;
}
