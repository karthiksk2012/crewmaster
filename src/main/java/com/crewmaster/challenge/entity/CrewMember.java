package com.crewmaster.challenge.entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

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
public class CrewMember {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Version
	private long version;

	@OneToMany
	private List<Shift> shifts;

	private String name;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

}
