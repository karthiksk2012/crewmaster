package com.crewmaster.challenge.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.crewmaster.challenge.exceptions.InvalidDataException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_process")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Version
	private long version;

	@NotNull
	private UUID companyId;

	private Instant startTime;

	private Instant endTime;

	@ElementCollection(targetClass = Shift.class)
	@Builder.Default
	private List<Shift> shifts = new ArrayList<>();

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	public Task validate() throws InvalidDataException {
		if (this.getStartTime().isAfter(this.getEndTime())) {
			throw new InvalidDataException("Start date time cannot be after end date time");
		}

		return this;
	}
}
