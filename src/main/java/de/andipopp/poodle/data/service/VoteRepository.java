package de.andipopp.poodle.data.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.Vote;

public interface VoteRepository extends JpaRepository<Vote<?,?>, UUID>{

	List<Vote<?, ?>> findByOwner(User owner);
}
