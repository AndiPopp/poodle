package de.andipopp.poodle.data.service;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.andipopp.poodle.data.entity.polls.Answer;

public interface AnswerRepository extends JpaRepository<Answer<?,?>, UUID>{


}
