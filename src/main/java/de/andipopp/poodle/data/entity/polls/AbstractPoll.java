package de.andipopp.poodle.data.entity.polls;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import de.andipopp.poodle.data.entity.AbstractEntity;

@Entity
public class AbstractPoll<O extends AbstractOption<?>> extends AbstractEntity {
	/* ==========
	 * = Fields =
	 * ========== */
	
	/**
	 * The poll's title
	 */
	@NotEmpty
	private String title;
	
	/**
	 * An optional poll description
	 */
	private String description;
	
	@OneToMany(cascade = CascadeType.PERSIST, targetEntity=AbstractOption.class)
	private List<O> options;
}
