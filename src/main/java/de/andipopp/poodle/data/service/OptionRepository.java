package de.andipopp.poodle.data.service;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.andipopp.poodle.data.entity.polls.AbstractOption;

public interface OptionRepository extends JpaRepository<AbstractOption<?,?>, UUID>{


}
