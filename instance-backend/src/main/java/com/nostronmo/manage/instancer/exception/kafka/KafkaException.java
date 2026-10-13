package com.nostronmo.manage.instancer.exception.kafka;

import org.springframework.http.HttpStatus;

import com.nostronmo.manage.instancer.exception.global.GlobalRuntimeException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class KafkaException extends GlobalRuntimeException {

	public KafkaException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public KafkaException(String message, Throwable cause) {
		super(message, cause, HttpStatus.CONFLICT);
	}

}
