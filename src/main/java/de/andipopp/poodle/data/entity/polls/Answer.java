package de.andipopp.poodle.data.entity.polls;

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
public class Answer<P extends AbstractPoll<P,O>, O extends AbstractOption<P,O>> extends AbstractEntity{

	/* ==========
	 * = Fields =
	 * ========== */
	
	public static final String YES_COLOR = "#537341";
	
	public static final String IF_NEED_BE_COLOR = "#D6B656";
	
	public static final String NO_COLOR = "#733432";
	
	public static final String NONE_COLOR = "#666666";
	
	/**
	 * The answered option
	 */
	@NotNull
	@ManyToOne(targetEntity=AbstractOption.class)
	private O option;
	
	/**
	 * The vote this answer belongs to
	 */
	@NotNull
	@ManyToOne(targetEntity=Vote.class)
	private Vote<P, O> vote;
	
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
	public Answer() {}
	
	/**
	 * 
	 */
	public Answer(@NotNull O option, @NotNull Vote<P,O> vote) {
		setOption(option);
		setVote(vote);
	}
	
	/**
	 * @param option
	 * @param value
	 */
	public Answer(@NotNull O option, @NotNull Vote<P,O> vote, AnswerType value) {
		this(option, vote);
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
	 * Getter for {@link #vote}
	 * @return the {@link #vote}
	 */
	public Vote<P, O> getVote() {
		return vote;
	}

	/**
	 * Setter for {@link #vote}
	 * @param vote the {@link #vote} to set
	 */
	public void setVote(Vote<P, O> vote) {
		this.vote = vote;
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
