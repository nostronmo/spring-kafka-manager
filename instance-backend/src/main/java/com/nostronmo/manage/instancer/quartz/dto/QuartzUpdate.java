package com.nostronmo.manage.instancer.quartz.dto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuartzUpdate(
		
		@JsonAlias( { "id" } )
		UUID query,
		String name,
		@NotNull(message = "Time list cannot be null")
	    @NotEmpty(message = "Time list cannot be empty")
	    List<String> time,
	    
	    Optional<String> kafkaTopic,
		Optional<String> targetService
		
		) {

}
