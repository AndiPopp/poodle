package de.andipopp.poodle.data.entity.polls;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.vaadin.flow.component.avatar.Avatar;

import de.andipopp.poodle.data.entity.AbstractEntity;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.util.InvalidException;

/**
 * A vote consists of a set of {@link AnswerType}s to a specific poll's options.
 * @author Andi Popp
 *
 */
@Entity
public class Vote<P extends AbstractPoll<P,O>, O extends AbstractOption<P,O>> extends AbstractEntity {

	/* ==========
	 * = Fields =
	 * ========== */
	
	public static final String NEW_VOTE_LABEL = "- new vote -";
	
	Random rng = new Random();
	
	/**
	 * The list of answers for this vote
	 */
	@NotNull
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Answer.class, orphanRemoval = true, mappedBy = "vote")
	@LazyCollection(LazyCollectionOption.FALSE)
	List<Answer<P,O>> answers; //TODO this should probably be a map, so every option gets max one answer, but I have no idea how to configure this in JPA/Hibernate
	
	@NotNull
	@ManyToOne(targetEntity=AbstractPoll.class)
	private AbstractPoll<P,O> parent; 
	
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
	private String displayName;
	
	/**
	 * Construct a new Vote with an empty hash set for {@link #answers}
	 */
	public Vote() {
		this.answers = new ArrayList<>();
	}
	
	/**
	 * Create a new empty vote for a given poll with the given owner
	 * @param parent the parent poll
	 * @param owner the vote's owner
	 * @param defaultAnswer the default answer to all options
	 */
	public Vote(@NotNull AbstractPoll<P,O> parent, User owner, AnswerType defaultAnswer) {
		this();
		this.parent = parent;
		this.owner = owner;
		for(Iterator<O> it = parent.getOptionIterator(); it.hasNext();) {
			answers.add(new Answer<P,O>(it.next(), this, defaultAnswer));
		}
	}
	
	/**
	 * Create a new empty vote for a given poll
	 * @param parent the parent poll
	 */
	public Vote(@NotNull AbstractPoll<P,O> parent) {
		this(parent, null, AnswerType.NONE);
	}
	
	/**
	 * Create a new empty vote for a given poll with the given owner
	 * @param parent the parent poll
	 * @param owner the vote's owner
	 */
	public Vote(@NotNull AbstractPoll<P,O> parent, User owner) {
		this(parent, owner, AnswerType.NONE);
	}
	

	/* =====================
	 * = Getters & Setters =
	 * ===================== */

	/**
	 * Getter for {@link #answers}
	 * @return the {@link #answers}
	 */
	public List<Answer<P,O>> getAnswers() {
		return answers;
	}

	/**
	 * Setter for {@link #answers}
	 * @param answers the {@link #answers} to set
	 */
	public void setAnswers(List<Answer<P,O>> answers) {
		this.answers = answers;
	}
	
	/**
	 * Adds a new answer for the given option, making sure all other answers for the option are removed
	 * @param option the option to answer
	 * @param answerType the type of answer
	 */
	public Answer<P,O> addAnswer(O option, AnswerType answerType) {
		//TODO this loop can be removed once I figure out how to replace 'answers' by a map
		for(Answer<P,O> answer : answers) {
			if (answer.getOption().equals(option)) answers.remove(answer);
		}
		Answer<P,O> answer = new Answer<P,O>(option, this, answerType);
		answers.add(answer);
		return answer;
	}
	
	/**
	 * Returns the answer to a given option as an {@link Optional}
	 * @param option the option
	 * @return an {@link Optional} with the answer if it is present, an empty {@link Optional} otherwise
	 */
	public Optional<Answer<P,O>> getAnswer(O option) {
		for(Answer<P,O> answer : answers) {
			if (answer.getOption().equals(option)) return Optional.of(answer);
		}
		return Optional.empty();
	}
	
	//TODO the following two methods can be simplified/deprecated once I figure out how to make 'answers' a map
	
	/**
	 * Get the {@link #answers} as a map
	 * @return the {@link #answers} as a map
	 */
	public Map<O, Answer<P,O>> getAnswerMap() {
		Map<O, Answer<P,O>> map = new HashMap<>();
		for(Answer<P,O> answer : answers) {
			map.put(answer.getOption(), answer);
		}
		return map;
	}
	
	/**
	 * Get the {@link #answers} as a map sorted by the given comparator
	 * @param comp the comparator to sort the map
	 * @return the {@link #answers} as a map sorted 
	 */
	public SortedMap<O, Answer<P,O>> getSortedAnswerMap(Comparator<O> comp) {
		SortedMap<O, Answer<P,O>> map = new TreeMap<>(comp);
		for(Answer<P,O> answer : answers) {
			map.put(answer.getOption(), answer);
		}
		return map;
	}
	
	/**
	 * Getter for {@link #parent}
	 * @return the {@link #parent}
	 */
	public AbstractPoll<P,O> getParent() {
		return parent;
	}
	
	/**
	 * Setter for {@link #parent}
	 * @param parent the {@link #parent} to set
	 */
	public void setParent(AbstractPoll<P,O> parent) {
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

	public void validateDisplayName(String displayName) throws InvalidException{
		//Check if all the inputs are complete
		if (displayName == null) throw new InvalidException("The display name may not be null");
		if (displayName.isEmpty()) throw new InvalidException("The display name may not be empty");
		if (displayName.equals(NEW_VOTE_LABEL)) throw new InvalidException("This display name is not allowed");
		for(Vote<P,O> vote : parent.getVotes()) {
			if (vote.getDisplayName().equals(displayName) && !vote.equals(this)) 
				throw new InvalidException("The display name is already in use");
		}
	}
	
	public void validateAndSetDisplayName(String displayName) throws InvalidException {
		validateDisplayName(displayName);
		setDisplayName(displayName);
	}
	
	public void validateAnswers() throws InvalidException {
		for(Answer<P, O> answer : answers) {
			switch (answer.getValue()) {
			case IF_NEED_BE:
				if (!parent.isEnableIfNeedBe()) throw new InvalidException("This poll does not allow 'if need be' answers.");
				break;
			case NO:
				break;
			case NONE:
				if (!parent.isEnableAbstain()) throw new InvalidException("This poll does not allow abstaining from options.");
				break;
			case YES:
				break;
			default:
				break;
			}
		}
	}
	
	public void validateAll() throws InvalidException {
		validateAnswers();
		validateDisplayName(displayName);
	}
	
	public void validateAllAndSetDisplayName(String displayName) throws InvalidException {
		validateAnswers();
		validateAndSetDisplayName(displayName);
	}
	
	public String getListLabel() {
		if (displayName != null) return displayName;
		if (owner != null) return owner.getName();
		return NEW_VOTE_LABEL;
	}
	
	public Avatar getAvatar() {
		if (owner != null) return owner.getAvatar();
		Avatar avatar = new Avatar();
		avatar.setColorIndex(rng.nextInt(6));
		if (displayName != null & !displayName.isEmpty()) avatar.setName(displayName);
		return avatar;
	}	
	
	public void fillInMissingAnswers() {
		SortedSet<O> sortedOptions = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
		sortedOptions.addAll(parent.getOptions());
		for(Answer<P,O> answer : answers) {
			if (sortedOptions.contains(answer.getOption())) {
				if (!sortedOptions.remove(answer.getOption())) throw new RuntimeException("Fatal Error: Failed to remove answer in fillInMissingAnswers!");
			}
		}
		for(O option : sortedOptions) {
			addAnswer(option, AnswerType.NONE);
		}
	}
}
