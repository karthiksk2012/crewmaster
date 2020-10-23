package com.crewmaster.challenge.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.crewmaster.challenge.dto.BookCrewMemberRequestDto;
import com.crewmaster.challenge.dto.GetShiftsResponse;
import com.crewmaster.challenge.dto.ReAssignShiftToMember;
import com.crewmaster.challenge.dto.ResponseDto;
import com.crewmaster.challenge.dto.ShiftResponse;
import com.crewmaster.challenge.entity.Shift;
import com.crewmaster.challenge.service.ShiftService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/tasks/{taskId}")
@RequiredArgsConstructor
public class ShiftController {

	private final ShiftService shiftService;

	@ApiOperation(value = "Get all shifts")
	@GetMapping(path = "/shifts")
	@ResponseBody
	public ResponseDto<GetShiftsResponse> getShifts(@PathVariable("taskId") UUID taskId) {
		List<ShiftResponse> shiftResponses = shiftService.getShifts(taskId).stream().map(this::mapShiftToResponse)
				.collect(Collectors.toList());

		return ResponseDto.<GetShiftsResponse>builder().data(GetShiftsResponse.builder().shifts(shiftResponses).build())
				.build();
	}

	@ApiOperation(value = "Delete a shift")
	@DeleteMapping(path = "/shifts/{shiftId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void cancelShift(@PathVariable("shiftId") UUID shiftId) throws Exception {
		shiftService.cancelShift(shiftId);
	}

	@ApiOperation(value = "Assign a Crew Member to a shift")
	@PostMapping(path = "/shifts/{shiftId}/book")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void bookCrewMember(@PathVariable("shiftId") UUID shiftId, @RequestBody @Valid BookCrewMemberRequestDto dto)
			throws Exception {
		shiftService.bookCrewMember(dto.getCrewMemberId(), shiftId);
	}

	@ApiOperation(value = "Re-assign all the shifts from one crew member to another")
	@PostMapping(path = "shifts/reassign-crew-member")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void reAssignCrewMember(@RequestBody @Valid ReAssignShiftToMember dto) throws Exception {
		shiftService.reAssignShiftsWithCrewMember(dto.getMemberToReplace(), dto.getMemberToReplaceWith());
	}

	private ShiftResponse mapShiftToResponse(Shift shift) {
		return ShiftResponse.builder().id(shift.getId()).memberId(shift.getMemberId()).taskId(shift.getTask().getId())
				.start(shift.getSlot().getStartTime()).end(shift.getSlot().getEndTime()).build();
	}
}
