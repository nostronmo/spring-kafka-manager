package com.nostronmo.manage.instancer.exception.quartz;

import org.springframework.http.HttpStatus;

import com.nostronmo.manage.instancer.exception.global.GlobalRuntimeException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class QuartzUnavailableException extends GlobalRuntimeException {

	public QuartzUnavailableException(String message) {
		super(message, HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	public QuartzUnavailableException(String message, Throwable throwable) {
		super(message, throwable, HttpStatus.SERVICE_UNAVAILABLE);
	}
	
}
