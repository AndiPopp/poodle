package de.andipopp.poodle.data.entity.polls;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import de.andipopp.poodle.data.entity.AbstractEntity;
import de.andipopp.poodle.data.entity.User;

/**
 * A vote consists of a set of {@link AnswerType}s to a specific poll's options.
 * @author Andi Popp
 *
 */
@Entity
public class Vote<O extends AbstractOption<? extends AbstractPoll<O>>> extends AbstractEntity {

	/* ==========
	 * = Fields =
	 * ========== */
	
	/**
	 * The list of answers for this vote
	 */
	@NotNull
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Answer.class)
	@LazyCollection(LazyCollectionOption.FALSE)
	List<Answer<O>> answers; //TODO this should probably be a map, so every option gets max one answer, but I have no idea how to configure this in JPA/Hibernate
	
	@NotNull
	@ManyToOne(targetEntity=AbstractPoll.class)
	private AbstractPoll<O> parent; 
	
	/**
	 * An optional user who owns this vote.
	 * If a vote has an owner, only the owner, an admin or the poll owner shall 
	 * be able to modify this vote. Otherwise everyone can modify the vote.
	 */
	@Nullable
	@ManyToOne
	private User owner;

	/**
	 * The name displayed alongside this vote.
	 */
	@NotNull
	private String displayName;
	
	/**
	 * Construct a new Vote with an empty hash set for {@link #answers}
	 */
	public Vote() {
		this.answers = new ArrayList<>();
		this.displayName = "";
	}
	
	/**
	 * Create a new empty vote for a given poll with the given owner
	 * @param parent the parent poll
	 * @param owner the vote's owner
	 * @param defaultAnswer the default answer to all options
	 */
	public Vote(@NotNull AbstractPoll<O> parent, User owner, AnswerType defaultAnswer) {
		this();
		this.parent = parent;
		this.owner = owner;
		for(Iterator<O> it = parent.getOptionIterator(); it.hasNext();) {
			answers.add(new Answer<O>(it.next(), defaultAnswer));
		}
	}
	
	/**
	 * Create a new empty vote for a given poll
	 * @param parent the parent poll
	 */
	public Vote(@NotNull AbstractPoll<O> parent) {
		this(parent, null, AnswerType.NONE);
	}
	
	/**
	 * Create a new empty vote for a given poll with the given owner
	 * @param parent the parent poll
	 * @param owner the vote's owner
	 */
	public Vote(@NotNull AbstractPoll<O> parent, User owner) {
		this(parent, owner, AnswerType.NONE);
	}
	
	



	/* =====================
	 * = Getters & Setters =
	 * ===================== */

	/**
	 * Getter for {@link #answers}
	 * @return the {@link #answers}
	 */
	public List<Answer<O>> getAnswers() {
		return answers;
	}

	/**
	 * Setter for {@link #answers}
	 * @param answers the {@link #answers} to set
	 */
	public void setAnswers(List<Answer<O>> answers) {
		this.answers = answers;
	}
	
	/**
	 * Adds a new answer for the given option, making sure all other answers for the option are removed
	 * @param option the option to answer
	 * @param answerType the type of answer
	 */
	public void addAnswer(O option, AnswerType answerType) {
		//TODO this loop can be removed once I figure out how to replace 'answers' by a map
		for(Answer<O> answer : answers) {
			if (answer.getOption().equals(option)) answers.remove(answer);
		}
		answers.add(new Answer<O>(option, answerType));
	}
	
	/**
	 * Returns the answer to a given option as an {@link Optional}
	 * @param option the option
	 * @return an {@link Optional} with the answer if it is present, an empty {@link Optional} otherwise
	 */
	public Optional<Answer<O>> getAnswer(O option) {
		for(Answer<O> answer : answers) {
			if (answer.getOption().equals(option)) return Optional.of(answer);
		}
		return Optional.empty();
	}
	
	//TODO the following two methods can be simplified/deprecated once I figure out how to make 'answers' a map
	
	/**
	 * Get the {@link #answers} as a map
	 * @return the {@link #answers} as a map
	 */
	public Map<O, Answer<O>> getAnswerMap() {
		Map<O, Answer<O>> map = new HashMap<>();
		for(Answer<O> answer : answers) {
			map.put(answer.getOption(), answer);
		}
		return map;
	}
	
	/**
	 * Get the {@link #answers} as a map sorted by the given comparator
	 * @param comp the comparator to sort the map
	 * @return the {@link #answers} as a map sorted 
	 */
	public SortedMap<O, Answer<O>> getSortedAnswerMap(Comparator<O> comp) {
		SortedMap<O, Answer<O>> map = new TreeMap<>(comp);
		for(Answer<O> answer : answers) {
			map.put(answer.getOption(), answer);
		}
		return map;
	}
	
	/**
	 * Getter for {@link #parent}
	 * @return the {@link #parent}
	 */
	public AbstractPoll<?> getParent() {
		return parent;
	}
	
	/**
	 * Setter for {@link #parent}
	 * @param parent the {@link #parent} to set
	 */
	public void setParent(AbstractPoll<O> parent) {
		this.parent = parent;
	}

	/**
	 * Getter for {@link #owner}
	 * @return the {@link #owner}
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Setter for {@link #owner}
	 * @param owner the {@link #owner} to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Getter for {@link #displayName}
	 * @return the {@link #displayName}
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Setter for {@link #displayName}
	 * @param displayName the {@link #displayName} to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}
