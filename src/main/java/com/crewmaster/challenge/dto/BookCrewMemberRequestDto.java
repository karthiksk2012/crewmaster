package com.crewmaster.challenge.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookCrewMemberRequestDto {

	@NotNull(message = "Crew Member Id cannot be null")
	private UUID crewMemberId;
}