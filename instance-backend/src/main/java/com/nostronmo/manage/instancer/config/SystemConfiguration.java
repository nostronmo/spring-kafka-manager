package com.nostronmo.manage.instancer.config;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.filter.ForwardedHeaderFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class SystemConfiguration {

	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

	@Bean
	DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(ZonedDateTime.now());
	}

}
