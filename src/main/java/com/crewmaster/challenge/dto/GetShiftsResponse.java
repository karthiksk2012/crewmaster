package com.crewmaster.challenge.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetShiftsResponse {
	List<ShiftResponse> shifts;
}