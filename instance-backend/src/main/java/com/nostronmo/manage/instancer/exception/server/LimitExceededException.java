package com.nostronmo.manage.instancer.exception.server;

import org.springframework.http.HttpStatus;

import com.nostronmo.manage.instancer.exception.global.GlobalRuntimeException;

@SuppressWarnings("serial")
public class LimitExceededException extends GlobalRuntimeException {

	public LimitExceededException(String message) {
		super(message, HttpStatus.METHOD_NOT_ALLOWED);
	}

	public LimitExceededException(String message, Throwable cause) {
		super(message, cause, HttpStatus.METHOD_NOT_ALLOWED);
	}

}
