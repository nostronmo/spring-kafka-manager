package com.nostronmo.manage.instancer.python.component;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.exception.sql.NotFoundException;
import com.nostronmo.manage.instancer.python.model.PythonServices;
import com.nostronmo.manage.instancer.python.repository.PythonRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PythonComponent {

	public PythonRepository pythonRepository;

	public PythonServices findById(UUID query) {
		return pythonRepository.findById(query)
				.orElseThrow(() -> new NotFoundException("Data with id: %s, was not found.".formatted(query)));
	}

}
