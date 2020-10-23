package com.crewmaster.challenge.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReAssignShiftToMember {

	@NotNull(message = "member id to replace cannot be null")
	private UUID memberToReplace;

	@NotNull(message = "member id to replace with cannot be null")
	private UUID memberToReplaceWith;
}