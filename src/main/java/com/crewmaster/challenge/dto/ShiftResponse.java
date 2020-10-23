package com.crewmaster.challenge.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShiftResponse {
	UUID id;
	UUID memberId;
	UUID taskId;
	Instant start;
	Instant end;
}