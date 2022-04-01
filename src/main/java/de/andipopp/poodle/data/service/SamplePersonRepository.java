package de.andipopp.poodle.data.service;

import de.andipopp.poodle.data.entity.SamplePerson;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

}