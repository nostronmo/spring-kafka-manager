package com.nostronmo.manage.instancer.global;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DurationListConverter implements AttributeConverter<List<Duration>, String> {

	private static final String SPLIT_CHAR = ",";

	@Override
	public String convertToDatabaseColumn(List<Duration> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return null;
		}
		return attribute.stream().filter(d -> d != null).map(Duration::toString)
				.collect(Collectors.joining(SPLIT_CHAR));
	}

	@Override
	public List<Duration> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().isEmpty()) {
			return Collections.emptyList();
		}

		return Arrays.stream(dbData.split(SPLIT_CHAR)).map(String::trim).filter(s -> !s.isEmpty())
				.map(this::parseDuration).collect(Collectors.toList());
	}

	public Duration parseDuration(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Duration value cannot be null or blank.");
		}

		String upperValue = value.trim().toUpperCase();

		if (upperValue.startsWith("P")) {
			return Duration.parse(upperValue);
		}

		if (upperValue.matches("^\\d+$")) {
			return Duration.ofSeconds(Long.parseLong(upperValue));
		}

		if (upperValue.matches("^\\d+MO$")) {
			long months = Long.parseLong(upperValue.replace("MO", ""));
			if (months > 12) {
				throw new IllegalArgumentException("Duration cannot exceed 12 months.");
			}
			return Duration.ofDays(months * 30);
		}

		if (upperValue.matches("^\\d+D$")) {
			return Duration.ofDays(Long.parseLong(upperValue.replace("D", "")));
		}
		if (upperValue.matches("^\\d+H$")) {
			return Duration.ofHours(Long.parseLong(upperValue.replace("H", "")));
		}
		if (upperValue.matches("^\\d+M$")) {
			return Duration.ofMinutes(Long.parseLong(upperValue.replace("M", "")));
		}
		if (upperValue.matches("^\\d+S$")) {
			return Duration.ofSeconds(Long.parseLong(upperValue.replace("S", "")));
		}

		throw new IllegalArgumentException("Invalid duration format: " + value);
	}
}