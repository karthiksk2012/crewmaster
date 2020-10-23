package com.crewmaster.challenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.crewmaster.challenge.dto.CrewMemberResponse;
import com.crewmaster.challenge.dto.ResponseDto;
import com.crewmaster.challenge.entity.CrewMember;
import com.crewmaster.challenge.service.CrewMemberService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/crew-members")
@RequiredArgsConstructor
public class CrewMemberController {

	private final CrewMemberService talentService;

	@ApiOperation(value = "Create a Crew Member")
	@PostMapping
	@ResponseBody
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseDto<CrewMemberResponse> createCrewMember() {
		CrewMember crewMember = talentService.createCrewMember();
		CrewMemberResponse crewMemberResponse = CrewMemberResponse.builder().memberId(crewMember.getId()).build();

		return ResponseDto.<CrewMemberResponse>builder().data(crewMemberResponse).build();
	}
}
