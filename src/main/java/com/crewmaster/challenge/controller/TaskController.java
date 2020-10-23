package com.crewmaster.challenge.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.crewmaster.challenge.dto.TaskRequestDto;
import com.crewmaster.challenge.dto.TaskResponseDto;
import com.crewmaster.challenge.dto.ResponseDto;
import com.crewmaster.challenge.entity.Task;
import com.crewmaster.challenge.service.TaskService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService jobService;

	@ApiOperation(value = "Create a Task", notes = "Set shiftGenerationStrategy to DEFAULT to auto generate shifts, CUSTOM to provide custom shift times")
	@PostMapping
	@ResponseBody
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseDto<TaskResponseDto> createTask(@RequestBody @Valid TaskRequestDto taskRequest) {
		Task task = jobService.createTask(taskRequest);
		TaskResponseDto taskResponse = TaskResponseDto.builder().taskId(task.getId()).build();

		return ResponseDto.<TaskResponseDto>builder().data(taskResponse).build();
	}

	@ApiOperation(value = "Delete a Task")
	@DeleteMapping(path = "/{taskId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void cancelTask(@PathVariable("taskId") UUID taskId) {
		jobService.cancelTask(taskId);
	}
}
