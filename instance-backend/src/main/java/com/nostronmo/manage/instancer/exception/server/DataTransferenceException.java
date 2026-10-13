package com.nostronmo.manage.instancer.exception.server;

import org.springframework.http.HttpStatus;

import com.nostronmo.manage.instancer.exception.global.GlobalRuntimeException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class DataTransferenceException extends GlobalRuntimeException {
	
	public DataTransferenceException(String msg) {
		super(msg, HttpStatus.EXPECTATION_FAILED);
	}
	
	public DataTransferenceException(String msg, Throwable cause) {
		super(msg, cause, HttpStatus.EXPECTATION_FAILED);
	}
	
}
