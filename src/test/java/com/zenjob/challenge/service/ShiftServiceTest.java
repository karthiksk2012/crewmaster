package com.zenjob.challenge.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.crewmaster.challenge.entity.Shift;
import com.crewmaster.challenge.repository.ShiftRepository;
import com.crewmaster.challenge.service.CrewMemberService;
import com.crewmaster.challenge.service.ShiftService;

public class ShiftServiceTest {

	private ShiftRepository shiftRepository;

	private CrewMemberService crewMemberService;

	private ShiftService shiftService;

    @Before
    public void setup() {
        shiftRepository = Mockito.mock(ShiftRepository.class);
        crewMemberService = Mockito.mock(CrewMemberService.class);
        
        shiftService = new ShiftService(shiftRepository, crewMemberService);
    }

	@Test
	public void testGetShiftsCallsFindById() {
		Shift shift = new Shift();
		List<Shift> shifts = new ArrayList<Shift>();
		shifts.add(shift);
		Mockito.when(shiftRepository.findAllByTask_Id(any(UUID.class))).thenReturn(new ArrayList<>());

		shiftService.getShifts(UUID.randomUUID());
	}

	@Test
	public void testBookCrewMemberCallsSaveOnShift() throws Exception {
		Shift shift = new Shift();
		Mockito.when(shiftRepository.getOne(any(UUID.class))).thenReturn(shift);
		Mockito.when(crewMemberService.canWorkShift(any(UUID.class), any(Shift.class))).thenReturn(true);
		
		shiftService.bookCrewMember(UUID.randomUUID(), UUID.randomUUID());
		verify(shiftRepository).save(shift);
	}

	@Test(expected = Exception.class)
	public void testBookTalentFailsIfTalentNotAvailable() throws Exception {
		Shift shift = new Shift();
		Mockito.when(shiftRepository.getOne(any(UUID.class))).thenReturn(shift);
		Mockito.when(crewMemberService.canWorkShift(any(UUID.class), any(Shift.class))).thenReturn(false);
		
		shiftService.bookCrewMember(UUID.randomUUID(), UUID.randomUUID());
	}

	@Test
	public void testBookTalentMultipleShiftsCallsSaveAll() throws Exception {
		Shift shift = new Shift();
		List<Shift> shifts = new ArrayList<Shift>();
		shifts.add(shift);

		Mockito.when(crewMemberService.canWorkMultipleShifts(any(UUID.class), anyList())).thenReturn(true);
		
		shiftService.bookCrewMember(UUID.randomUUID(), shifts);
		verify(shiftRepository).saveAll(anyList());
	}

	@Test(expected = Exception.class)
	public void testBookTalentMultipleShiftsFailsIfTalentNotAvailable() throws Exception {
		Shift shift = new Shift();
		List<Shift> shifts = new ArrayList<Shift>();
		shifts.add(shift);

		Mockito.when(crewMemberService.canWorkMultipleShifts(any(UUID.class), anyList())).thenReturn(false);
		
		shiftService.bookCrewMember(UUID.randomUUID(), shifts);
	}
	
	@Test
	public void testReAssignShiftsWithTalentBooksTalent() throws Exception {
		Mockito.when(shiftRepository.findAllByCrewMemberId(any(UUID.class))).thenReturn(new ArrayList<Shift>());
		Mockito.when(crewMemberService.canWorkMultipleShifts(any(UUID.class), anyList())).thenReturn(true);

		shiftService.reAssignShiftsWithCrewMember(UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = Exception.class)
	public void testCancelShiftRequiresMinimumShiftsOnAJob() throws Exception {
		Shift shift = new Shift();
		List<Shift> shifts = new ArrayList<Shift>();
		shifts.add(shift);
		Mockito.when(shiftRepository.findAllByTask_Id(any(UUID.class))).thenReturn(shifts);
		Mockito.when(shiftRepository.findById(any(UUID.class))).thenReturn(Optional.of(shift));

		shiftService.cancelShift(UUID.randomUUID());
	}
}
