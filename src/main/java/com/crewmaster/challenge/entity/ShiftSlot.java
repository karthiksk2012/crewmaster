package com.crewmaster.challenge.entity;

import java.time.Instant;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShiftSlot {

	private Instant startTime;

	private Instant endTime;
}
