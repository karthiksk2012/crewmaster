package com.zenjob.challenge.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.crewmaster.challenge.entity.Shift;
import com.crewmaster.challenge.entity.ShiftSlot;
import com.crewmaster.challenge.entity.CrewMember;
import com.crewmaster.challenge.repository.ShiftRepository;
import com.crewmaster.challenge.repository.CrewMemberRepository;
import com.crewmaster.challenge.service.CrewMemberService;

@RunWith(MockitoJUnitRunner.class)
public class CrewMemberServiceTest {

	private CrewMemberRepository memberRepository;

	private ShiftRepository shiftRepository;

	private CrewMemberService memberService;

    @Before
    public void setup() {
        memberRepository = Mockito.mock(CrewMemberRepository.class);
        shiftRepository = Mockito.mock(ShiftRepository.class);
        memberService = new CrewMemberService(memberRepository, shiftRepository);
    }
    
    @Test
	public void testCreateCrewMember() {
		memberService.createCrewMember();
		verify(memberRepository).save(any(CrewMember.class));
	}

	@Test
	public void testCrewMemberCanWorkShift() {
		CrewMember member = new CrewMember();
		member.setShifts(generateTestAlreadyOccupiedSlots());
		Shift toOccupy = generateTestSampleSlotsToOccupy().get(0);
		Optional<CrewMember> opMember = Optional.of(member);
		Mockito.when(this.shiftRepository.findAllByCrewMemberId(any())).thenReturn(generateTestAlreadyOccupiedSlots());
		Mockito.when(this.memberRepository.findById(any())).thenReturn(opMember);

		assertTrue(this.memberService.canWorkShift(member.getId(), toOccupy));
	}

	@Test
	public void testCrewMemberCanNotWorkShift() {
		CrewMember member = new CrewMember();
		member.setShifts(generateTestAlreadyOccupiedSlots());
		Shift toOccupy = generateTestSampleSlotsToOccupy().get(1);
		Optional<CrewMember> opMember = Optional.of(member);
		Mockito.when(this.shiftRepository.findAllByCrewMemberId(any())).thenReturn(generateTestAlreadyOccupiedSlots());
		Mockito.when(this.memberRepository.findById(any())).thenReturn(opMember);

		assertFalse(memberService.canWorkShift(member.getId(), toOccupy));
	}

	@Test
	public void testMemberCanNotWorkShiftNotEnoughBreakTime() {
		CrewMember member = new CrewMember();
		member.setShifts(generateTestAlreadyOccupiedSlots());
		Shift toOccupy = generateTestSampleSlotsToOccupy().get(2);
		Optional<CrewMember> opMember = Optional.of(member);
		Mockito.when(this.shiftRepository.findAllByCrewMemberId(any())).thenReturn(generateTestAlreadyOccupiedSlots());
		Mockito.when(this.memberRepository.findById(any())).thenReturn(opMember);

		assertFalse(memberService.canWorkShift(member.getId(), toOccupy));
	}

	@Test
	public void testMemberCanWorkShiftAfterEnoughBreakTime() {
		CrewMember crewMember = new CrewMember();
		crewMember.setShifts(generateTestAlreadyOccupiedSlots());
		Shift toOccupy = generateTestSampleSlotsToOccupy().get(3);
		Optional<CrewMember> opMember = Optional.of(crewMember);
		Mockito.when(this.shiftRepository.findAllByCrewMemberId(any())).thenReturn(generateTestAlreadyOccupiedSlots());
		Mockito.when(this.memberRepository.findById(any())).thenReturn(opMember);

		assertTrue(memberService.canWorkShift(crewMember.getId(), toOccupy));
	}
	
	private ArrayList<Shift> generateTestSampleSlotsToOccupy(){
	    /**
	     * This slot is available and can be booked 	
	     */
		Instant startTime1 = Instant.parse("2007-12-04T12:00:00.00Z");
		Instant endTime1 = Instant.parse("2007-12-04T16:00:00.00Z");
		ShiftSlot slot1 = new ShiftSlot(startTime1, endTime1);
		Shift shift1 = new Shift().setSlot(slot1);

		/**
		 * This slot overlaps with slot2 in occupied slots and cannot be booked
		 */
		Instant startTime2 = Instant.parse("2007-12-03T20:00:00.00Z");
		Instant endTime2 = Instant.parse("2007-12-03T23:00:00.00Z");
		ShiftSlot slot2 = new ShiftSlot(startTime2, endTime2);
		Shift shift2 = new Shift().setSlot(slot2);

		/**
		 * This slot is not minimum hours after slot3 in occupied slots and cannot be booked
		 */
		Instant startTime3 = Instant.parse("2007-12-05T13:00:00.00Z");
		Instant endTime3 = Instant.parse("2007-12-05T17:00:00.00Z");
		ShiftSlot slot3 = new ShiftSlot(startTime3, endTime3);
		Shift shift3 = new Shift().setSlot(slot3);

		/**
		 * This slot is exactly 6 hours after slot4 in occupied slots and can be booked
		 */
		Instant startTime4 = Instant.parse("2007-12-07T20:00:00.00Z");
		Instant endTime4 = Instant.parse("2007-12-07T23:00:00.00Z");
		ShiftSlot slot4 = new ShiftSlot(startTime4, endTime4);
		Shift shift4 = new Shift().setSlot(slot4);

		return new ArrayList<Shift>(Arrays.asList(shift1, shift2, shift3, shift4));
	}

	private ArrayList<Shift> generateTestAlreadyOccupiedSlots() {

		Instant startTime1 = Instant.parse("2007-12-03T10:00:00.00Z");
		Instant endTime1 = Instant.parse("2007-12-03T14:00:00.00Z");
		ShiftSlot slot1 = new ShiftSlot(startTime1, endTime1);
		Shift shift1 = new Shift().setSlot(slot1);

		Instant startTime2 = Instant.parse("2007-12-03T21:15:00.00Z");
		Instant endTime2 = Instant.parse("2007-12-04T02:15:00.00Z");
		ShiftSlot slot2 = new ShiftSlot(startTime2, endTime2);
		Shift shift2 = new Shift().setSlot(slot2);

		Instant startTime3 = Instant.parse("2007-12-05T05:00:00.00Z");
		Instant endTime3 = Instant.parse("2007-12-05T09:00:00.00Z");
		ShiftSlot slot3 = new ShiftSlot(startTime3, endTime3);
		Shift shift3 = new Shift().setSlot(slot3);

		Instant startTime4 = Instant.parse("2007-12-07T10:00:00.00Z");
		Instant endTime4 = Instant.parse("2007-12-07T14:00:00.00Z");
		ShiftSlot slot4 = new ShiftSlot(startTime4, endTime4);
		Shift shift4 = new Shift().setSlot(slot4);

		Instant startTime5 = Instant.parse("2007-12-08T12:00:00.00Z");
		Instant endTime5 = Instant.parse("2007-12-08T18:00:00.00Z");
		ShiftSlot slot5 = new ShiftSlot(startTime5, endTime5);
		Shift shift5 = new Shift().setSlot(slot5);
	
		return new ArrayList<Shift>(Arrays.asList(shift1, shift2, shift3, shift4, shift5));
	}
}