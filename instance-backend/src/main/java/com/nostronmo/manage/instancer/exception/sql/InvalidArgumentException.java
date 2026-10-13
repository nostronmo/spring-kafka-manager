package com.nostronmo.manage.instancer.exception.sql;

import org.springframework.http.HttpStatus;

import com.nostronmo.manage.instancer.exception.global.GlobalRuntimeException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class InvalidArgumentException extends GlobalRuntimeException {

	public InvalidArgumentException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

	public InvalidArgumentException(String message, Throwable cause) {
		super(message, cause, HttpStatus.BAD_REQUEST);
	}

}
