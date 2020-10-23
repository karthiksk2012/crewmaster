package com.crewmaster.challenge.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crewmaster.challenge.dto.TimeSlot;
import com.crewmaster.challenge.entity.Task;
import com.crewmaster.challenge.entity.Shift;
import com.crewmaster.challenge.entity.ShiftSlot;
import com.crewmaster.challenge.exceptions.InvalidDataException;
import com.crewmaster.challenge.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ShiftService {

	private static final Integer DEFAULT_SHIFT_START_TIME = 8;
	private static final Integer DEFAULT_SHIFT_END_TIME = 16;
	private static final Integer MINIMUM_SLOTS_PER_TASK = 1;
	private static final Integer MINIMUM_SLOT_DURATION_IN_HOURS = 2;
	private static final Integer MAXIMUM_SLOT_DURATION_IN_HOURS = 8;
	private static final Integer MINIMUM_SHIFTS_IN_TASK = 1;

	private final ShiftRepository shiftRepository;

	private final CrewMemberService crewMemberService;

	public List<Shift> getShifts(UUID id) {
		return shiftRepository.findAllByTask_Id(id);
	}

	public void bookCrewMember(UUID crewMember, UUID shiftId) throws Exception {
		Shift shift = shiftRepository.getOne(shiftId);
		if (!crewMemberService.canWorkShift(crewMember, shift)) {
			throw new Exception("Talent is unavailable for this shift");
		}
		shift.setMemberId(crewMember);
		shiftRepository.save(shift);
	}

	public void bookCrewMember(UUID crewMember, List<Shift> shifts) throws Exception {
		if (!crewMemberService.canWorkMultipleShifts(crewMember, shifts)) {
			throw new Exception("Crew Member is unavailable for all the shifts");
		}
		for (Shift s : shifts) {
			s.setMemberId(crewMember);
		}
		shiftRepository.saveAll(shifts);
	}

	public void reAssignShiftsWithCrewMember(UUID memberToReplace, UUID memberToReplaceWith) throws Exception {
		List<Shift> shifts = shiftRepository.findAllByCrewMemberId(memberToReplace);
		bookCrewMember(memberToReplaceWith, shifts);
	}

	public void cancelShift(UUID shiftId) throws Exception {
		Optional<Shift> shift = shiftRepository.findById(shiftId);
		if (!shift.isPresent()) {
			throw new InvalidDataException("Shift not found");
		}
		Integer shiftCount = shiftRepository.findAllByTask_Id(shift.get().getTask().getId()).size();
		if (shiftCount <= MINIMUM_SHIFTS_IN_TASK) {
			throw new Exception(
					"Task needs to have " + MINIMUM_SHIFTS_IN_TASK + " minimum number of shifts to be deleted");
		}
		shiftRepository.delete(shift.get());
	}

	private List<ShiftSlot> generateSlots(List<TimeSlot> userProvidedSlots) {
		if (userProvidedSlots.size() < MINIMUM_SLOTS_PER_TASK) {
			throw new InvalidDataException("Please provide  a minimum " + MINIMUM_SLOTS_PER_TASK + " slot for the job");
		}
		return validateAndMapCustomSlots(userProvidedSlots);
	}

	private List<ShiftSlot> generateSlots(LocalDate start, LocalDate end) {
		long numberOfDays = ChronoUnit.DAYS.between(start, end);

		return LongStream.range(0, numberOfDays).mapToObj(day -> start.plus(day, ChronoUnit.DAYS))
				.map(this::buildSlotForTheDay).collect(Collectors.toList());
	}

	private List<ShiftSlot> validateAndMapCustomSlots(List<TimeSlot> customTimeSlots) {
		List<ShiftSlot> shiftSlots = new ArrayList<ShiftSlot>();
		for (TimeSlot t : customTimeSlots) {
			if (t.getStart().isAfter(t.getEnd())) {
				throw new InvalidDataException("Start time cannot be after end time");
			}

			long duration = Duration.between(t.getStart(), t.getEnd()).toHours();
			if (duration < MINIMUM_SLOT_DURATION_IN_HOURS
					|| duration > MAXIMUM_SLOT_DURATION_IN_HOURS) {
				throw new InvalidDataException("Slot should be minimum of " + MINIMUM_SLOT_DURATION_IN_HOURS
						+ "hours and max of" + MAXIMUM_SLOT_DURATION_IN_HOURS + " hours");
			}
			shiftSlots.add(new ShiftSlot(t.getStart().toInstant(ZoneOffset.UTC), t.getEnd().toInstant(ZoneOffset.UTC)));
		}

		return shiftSlots;
	}

	private ShiftSlot buildSlotForTheDay(LocalDate date) {
		Instant defaultStartTime = date.atTime(DEFAULT_SHIFT_START_TIME, 0).toInstant(ZoneOffset.UTC);
		Instant defaultEndTime = date.atTime(DEFAULT_SHIFT_END_TIME, 0).toInstant(ZoneOffset.UTC);

		return new ShiftSlot(defaultStartTime, defaultEndTime);
	}

	public void generateShifts(List<TimeSlot> customSlots, Task task) {
		List<ShiftSlot> slots = generateSlots(customSlots);
		List<Shift> shifts = slots.stream().map(slot -> buildShift(slot, task)).collect(Collectors.toList());
		shiftRepository.saveAll(shifts);
	}

	public void generateShifts(LocalDate start, LocalDate end, Task task) {
		List<ShiftSlot> slots = generateSlots(start, end);
		List<Shift> shifts = slots.stream().map(slot -> buildShift(slot, task)).collect(Collectors.toList());
		shiftRepository.saveAll(shifts);
	}

	private Shift buildShift(ShiftSlot slot, Task task) {
		return Shift.builder().task(task).slot(slot).build();
	}
}
