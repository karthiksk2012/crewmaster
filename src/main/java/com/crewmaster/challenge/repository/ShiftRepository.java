package com.crewmaster.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crewmaster.challenge.entity.Shift;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

	List<Shift> findAllByTask_Id(UUID uuid);

	List<Shift> findAllByCrewMemberId(UUID memberId);
}
