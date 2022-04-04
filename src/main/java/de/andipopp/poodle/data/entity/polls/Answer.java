package de.andipopp.poodle.data.entity.polls;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import de.andipopp.poodle.data.entity.AbstractEntity;

/**
 * A reply to single poll option.
 * This class wraps a single option ({@link AbstractOption}) with and an {@link AnswerType}}.
 * @author Andi Popp
 *
 */
@Entity
public class Answer<O extends AbstractOption<? extends AbstractPoll<O>>> extends AbstractEntity{

	/* ==========
	 * = Fields =
	 * ========== */
	
	/**
	 * The answered option
	 */
	@NotNull
	@ManyToOne(cascade = CascadeType.PERSIST, targetEntity=AbstractOption.class)
	private O option;
	
	/**
	 * The type of answer given to the option
	 */
	private AnswerType value = AnswerType.NONE;

	
	/* ================
	 * = Constructors =
	 * ================ */
	
	/**
	 * 
	 */
	public Answer() {
		super();
	}
	
	/**
	 * 
	 */
	public Answer(@NotNull O option) {
		this.option = option;
	}
	
	/**
	 * @param option
	 * @param value
	 */
	public Answer(@NotNull O option, AnswerType value) {
		this(option);
		this.value = value;
	}
	
	

	/* =====================
	 * = Getters & Setters =
	 * ===================== */
	
	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	public O getOption() {
		return option;
	}

	/**
	 * Setter for {@link #option}
	 * @param option the {@link #option} to set
	 */
	public void setOption(O option) {
		this.option = option;
	}

	/**
	 * Getter for {@link #value}
	 * @return the {@link #value}
	 */
	public AnswerType getValue() {
		return value;
	}

	/**
	 * Setter for {@link #value}
	 * @param value the {@link #value} to set
	 */
	public void setValue(AnswerType value) {
		this.value = value;
	}
		
}
