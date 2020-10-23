package com.crewmaster.challenge.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskRequestDto {

	@NotNull(message = "CompanyId cannot be null")
	private UUID companyId;

	@NotNull(message = "start cannot be null")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate start;

	@NotNull(message = "end cannot be null")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate end;

	@NotNull(message = "shiftGenerationStrategy cannot be null")
	private ShiftGenerationStrategy shiftGenerationStrategy;

	/**
	 * If ShiftGenerationStrategy is CUSTOM, this property should contain custom
	 * slots
	 */
	private List<TimeSlot> customTimeSlots;
}