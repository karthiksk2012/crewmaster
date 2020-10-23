package com.crewmaster.challenge.entity;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Shift {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Version
	private long version;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@NotNull
	private Task task;

	private UUID memberId;

	@Embedded
	private ShiftSlot slot;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;
}
