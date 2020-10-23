package com.crewmaster.challenge.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crewmaster.challenge.entity.Shift;
import com.crewmaster.challenge.entity.ShiftSlot;
import com.crewmaster.challenge.entity.CrewMember;
import com.crewmaster.challenge.exceptions.InvalidDataException;
import com.crewmaster.challenge.repository.ShiftRepository;
import com.crewmaster.challenge.repository.CrewMemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class CrewMemberService {

	private final static Integer MINIMUM_HOURS_BETWEEN_SHIFTS = 6;

	private final CrewMemberRepository crewMemberRepository;

	private final ShiftRepository shiftRepository;

	public CrewMember createCrewMember() {
		CrewMember crewMember = new CrewMember();
		return crewMemberRepository.save(crewMember);
	}

	public Boolean canWorkShift(UUID memberId, Shift shift) {
		Optional<CrewMember> crewMember = crewMemberRepository.findById(memberId);
		if (!crewMember.isPresent()) {
			throw new InvalidDataException("Crew Member not found");
		}

		return isCrewMemberAvailableForSlot(crewMember.get(), shift.getSlot());
	}

	public Boolean canWorkMultipleShifts(UUID memberId, List<Shift> shifts) {
		return shifts.stream().noneMatch(shift -> canWorkShift(memberId, shift) == false);
	}

	public Boolean isCrewMemberAvailableForSlot(CrewMember talent, ShiftSlot shiftSlot) {
		Set<Instant> memberUnavailabilityHours = getMemberUnAvailableHours(talent);
		Set<Instant> slotHours = getSlotHours(shiftSlot);
		return slotHours.stream().noneMatch(memberUnavailabilityHours::contains);
	}

	private Set<Instant> getSlotHours(ShiftSlot shiftSlot) {
		Instant slotBegin = shiftSlot.getStartTime().truncatedTo(ChronoUnit.HOURS);
		Instant slotEnd = shiftSlot.getEndTime().truncatedTo(ChronoUnit.HOURS);
		return LongStream.range(0, Duration.between(slotBegin, slotEnd).toHours())
				.mapToObj(hour -> slotBegin.plus(hour, ChronoUnit.HOURS)).collect(Collectors.toSet());
	}

	private Set<Instant> getMemberUnAvailableHours(CrewMember member) {
		return shiftRepository.findAllByCrewMemberId(member.getId()).stream()
				.map(shift -> unavailableHours(shift.getSlot())).flatMap(Set::stream).collect(Collectors.toSet());
	}

	private Set<Instant> unavailableHours(ShiftSlot slot) {
		Instant unavailableTimeBegin = slot.getStartTime().minus(MINIMUM_HOURS_BETWEEN_SHIFTS, ChronoUnit.HOURS)
				.truncatedTo(ChronoUnit.HOURS);
		Instant unavailableTimeEnd = slot.getEndTime().plus(MINIMUM_HOURS_BETWEEN_SHIFTS, ChronoUnit.HOURS)
				.truncatedTo(ChronoUnit.HOURS);

		Set<Instant> occupiedHours = LongStream
				.range(0, Duration.between(unavailableTimeBegin, unavailableTimeEnd).toHours())
				.mapToObj(hour -> unavailableTimeBegin.plus(hour, ChronoUnit.HOURS)).collect(Collectors.toSet());

		return occupiedHours;
	}

}
