package com.crewmaster.challenge.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crewmaster.challenge.entity.CrewMember;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, UUID> {
}
