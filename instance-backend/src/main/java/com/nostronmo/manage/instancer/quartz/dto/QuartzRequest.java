package com.nostronmo.manage.instancer.quartz.dto;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuartzRequest(
		
		String name,
		
		@JsonAlias( { "duration", "time" } )
		@NotNull(message = "Time list cannot be null")
	    @NotEmpty(message = "Time list cannot be empty")
	    List<String> time,

	    Optional<String> kafkaTopic,
		Optional<String> targetService
		
		) {

}
