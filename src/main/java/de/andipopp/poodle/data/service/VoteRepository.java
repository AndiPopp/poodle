package de.andipopp.poodle.data.service;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.andipopp.poodle.data.entity.polls.Vote;

public interface VoteRepository extends JpaRepository<Vote<?>, UUID>{


}
