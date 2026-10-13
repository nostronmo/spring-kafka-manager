package com.nostronmo.manage.instancer.exception.global;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ GlobalRuntimeException.class })
	public ProblemDetail handleCustomExceptions(GlobalRuntimeException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
		problem.setTitle(ex.getClass().getSimpleName());
		return problem;
	}

	@ExceptionHandler({ GlobalDateTimeException.class })
	public ProblemDetail handleGlobalDateTime(GlobalDateTimeException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
		problem.setTitle("Date Time Error");
		return problem;
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleMissingParams(MissingServletRequestParameterException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, 
				"Missing required parameter: " + ex.getParameterName());
		problem.setTitle("Missing Request Parameter");
		return problem;
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, 
				"Method not supported: " + ex.getMethod() + ". The supported methods are: " + ex.getSupportedHttpMethods());
		problem.setTitle("Method Not Allowed");
		return problem;
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handle(MethodArgumentNotValidException exp) {
		Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream()
	            .collect(Collectors.toMap(
	                    FieldError::getField,
	                    err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid",
	                    (existing, replacement) -> existing
	            ));
	    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
	    problem.setTitle("Invalid Request Parameters");
	    problem.setProperty("invalid_fields", errors);
		return problem;
	}

}
