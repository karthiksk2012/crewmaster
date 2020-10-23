package com.zenjob.challenge.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.crewmaster.challenge.dto.TaskRequestDto;
import com.crewmaster.challenge.dto.ShiftGenerationStrategy;
import com.crewmaster.challenge.dto.TimeSlot;
import com.crewmaster.challenge.entity.Task;
import com.crewmaster.challenge.exceptions.InvalidDataException;
import com.crewmaster.challenge.repository.TaskRepository;
import com.crewmaster.challenge.service.TaskService;
import com.crewmaster.challenge.service.ShiftService;

public class TaskServiceTest {

	private TaskRepository taskRepository;

	private ShiftService shiftService;

	private TaskService taskService;

    @Before
    public void setup() {
        taskRepository = Mockito.mock(TaskRepository.class);
        shiftService = Mockito.mock(ShiftService.class);
        
        taskService = new TaskService(taskRepository, shiftService);
    }
	
	@Test
	public void testCreateTaskCallSave() {
		TaskRequestDto dto = getSampleTaskRequest();
		taskService.createTask(dto);
		verify(taskRepository).save(any(Task.class));
	}

    @Test(expected = InvalidDataException.class)
	public void testCreateTaskWithInvalidDatesThrowsException() {
		TaskRequestDto dto = getSampleTaskRequestWithInvalidDates();
		taskService.createTask(dto);
	}

	@Test
	public void testCreateTaskWithDefaultStrategyCallsCorrectShiftGeneration() {
		TaskRequestDto dto = getSampleTaskRequest();
		taskService.createTask(dto);

		verify(shiftService).generateShifts(any(LocalDate.class), any(LocalDate.class), any(Task.class));
	}

	@Test
	public void testCreateTaskWithCustomStrategyCallsCorrectShiftGeneration() {
		TaskRequestDto dto = getSampleTaskRequestWithCustomShiftGenerationStrategy();
		taskService.createTask(dto);

		verify(shiftService).generateShifts(anyList(), any(Task.class));
	}

	@Test
	public void testCancelTaskCallsDelete() {
		Task j = new Task();
		Optional<Task> jop = Optional.of(j);
		Mockito.when(taskRepository.findById(any(UUID.class))).thenReturn(jop);
		taskService.cancelTask(UUID.randomUUID());

		verify(taskRepository).delete(any(Task.class));
	}

	private TaskRequestDto getSampleTaskRequest() {
		TaskRequestDto dto = new TaskRequestDto();
		dto.setCompanyId(UUID.randomUUID());
		dto.setStart(LocalDate.now());
		dto.setEnd(LocalDate.now().plusDays(2));
		dto.setShiftGenerationStrategy(ShiftGenerationStrategy.DEFAULT);
		
		return dto;
	}

	private TaskRequestDto getSampleTaskRequestWithInvalidDates() {
		TaskRequestDto dto = new TaskRequestDto();
		dto.setCompanyId(UUID.randomUUID());
		dto.setStart(LocalDate.now().plusDays(2));
		dto.setEnd(LocalDate.now());
		dto.setShiftGenerationStrategy(ShiftGenerationStrategy.DEFAULT);
		
		return dto;
	}

	private TaskRequestDto getSampleTaskRequestWithCustomShiftGenerationStrategy() {
		TaskRequestDto dto = new TaskRequestDto();
		dto.setCompanyId(UUID.randomUUID());
		dto.setStart(LocalDate.now());
		dto.setEnd(LocalDate.now().plusDays(2));
		dto.setShiftGenerationStrategy(ShiftGenerationStrategy.CUSTOM);
		TimeSlot slot1 = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plus(5, ChronoUnit.HOURS));
		List<TimeSlot> customSlots = new ArrayList<TimeSlot>();
		customSlots.add(slot1);
		dto.setCustomTimeSlots(customSlots);
		
		return dto;
	}

}
