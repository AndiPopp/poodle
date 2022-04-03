package de.andipopp.poodle.data.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;

public interface PollRepository extends JpaRepository<AbstractPoll<?>, UUID>{

	List<AbstractPoll> findByOwner(User owner);
	
	List<AbstractPoll> findByOwnerOrderByCreateDateDesc(User owner);
	
	List<AbstractPoll> findAllByOrderByCreateDateDesc();
}
