package com.crewmaster.challenge.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskResponseDto {
	UUID taskId;
}