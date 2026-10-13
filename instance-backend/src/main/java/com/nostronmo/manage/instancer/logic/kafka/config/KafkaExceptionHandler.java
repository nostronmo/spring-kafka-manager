package com.nostronmo.manage.instancer.logic.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.nostronmo.manage.instancer.exception.sql.DataViolationException;
import com.nostronmo.manage.instancer.exception.sql.InvalidArgumentException;
import com.nostronmo.manage.instancer.exception.sql.InvalidDateException;
import com.nostronmo.manage.instancer.exception.sql.NotFoundException;

@Lazy(false)
@Configuration
public class KafkaExceptionHandler {

	@Bean("serviceErrorHandler")
	DefaultErrorHandler webscrapingErrorHandler() {
		// DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaOperations);
		
		FixedBackOff backOff = new FixedBackOff(2000L, 3);
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);
		
		errorHandler.addNotRetryableExceptions(
				IllegalArgumentException.class, 
				NullPointerException.class,
				NotFoundException.class,
				DataViolationException.class,
				InvalidDateException.class,
				InvalidArgumentException.class
		);
		
		return errorHandler;
	}
	
}
