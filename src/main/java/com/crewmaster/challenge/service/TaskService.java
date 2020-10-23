package com.crewmaster.challenge.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crewmaster.challenge.dto.TaskRequestDto;
import com.crewmaster.challenge.dto.ShiftGenerationStrategy;
import com.crewmaster.challenge.entity.Task;
import com.crewmaster.challenge.exceptions.InvalidDataException;
import com.crewmaster.challenge.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class TaskService {

	private static final Integer DEFAULT_DAY_START_TIME = 8;
	private static final Integer DEFAULT_DAY_END_TIME = 17;

	private final TaskRepository taskRepository;

	private final ShiftService shiftService;

	public Task createTask(TaskRequestDto request) throws InvalidDataException {
		Task task = Task.builder().companyId(request.getCompanyId())
				.startTime(localDateToTime(request.getStart(), DEFAULT_DAY_START_TIME))
				.endTime(localDateToTime(request.getEnd(), DEFAULT_DAY_END_TIME)).build().validate();
		taskRepository.save(task);

		if (request.getShiftGenerationStrategy().equals(ShiftGenerationStrategy.CUSTOM)) {
			shiftService.generateShifts(request.getCustomTimeSlots(), task);
		} else {
			shiftService.generateShifts(request.getStart(), request.getEnd(), task);
		}

		return task;
	}

	public void cancelTask(UUID taskId) {
		Optional<Task> task = taskRepository.findById(taskId);
		if (!task.isPresent()) {
			throw new InvalidDataException("Task not found");
		}

		taskRepository.delete(task.get());
	}

	private Instant localDateToTime(LocalDate date, int hour) {
		return date.atTime(hour, 0, 0).toInstant(ZoneOffset.UTC);
	}
}
