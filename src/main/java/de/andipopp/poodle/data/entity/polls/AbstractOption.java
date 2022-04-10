/**
 * 
 */
package de.andipopp.poodle.data.entity.polls;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractEntity;
import de.andipopp.poodle.views.poll.OptionListItem;

/**
 * An abstract representation of an option in a poll
 * @author Andi Popp
 */
@Entity
//public abstract class AbstractOption<P extends AbstractPoll<? extends AbstractOption<P>>> extends AbstractEntity {
public abstract class AbstractOption<P extends AbstractPoll<P,O>, O extends AbstractOption<P,O>> extends AbstractEntity {	

	@ManyToOne(targetEntity=AbstractPoll.class)
	private P parent;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Answer.class, mappedBy = "option", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<Answer<P,O>> answers;
	
	/**
	 * An optional human readable title
	 */
	private String title;

	/* ================
	 * = Constructors = 
	 * ================ */

	/**
	 * Empty constructor
	 */
	public AbstractOption() {
		this.answers = new HashSet<>();
	}

	/**
	 * Construct a new Option with a given title
	 * @param title value for {@link #title}
	 */
	public AbstractOption(String title) {
		this();
		this.title = title;
	}
	
	/* ==============================
	 * = Getters, Setters & Similar =
	 * ============================== */
	
	/**
	 * Getter for {@link #title}
	 * @return the {@link #title}
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter for {@link #title}. Cleans all HTML.
	 * @param title the {@link #title} to set
	 */
	public void setTitle(String title) {
		this.title = Jsoup.clean(title, Safelist.none());
	}

	/**
	 * Getter for {@link #parent}
	 * @return the {@link #parent}
	 */
	public P getParent() {
		return parent;
	}

	/**
	 * Setter for {@link #parent}
	 * @param parent the {@link #parent} to set
	 */
	public void setParent(P parent) {
		this.parent = parent;
	}

	/**
	 * Getter for {@link #answers}
	 * @return the {@link #answers}
	 */
	public Set<Answer<P,O>> getAnswers() {
		return answers;
	}

	/**
	 * Setter for {@link #answers}
	 * @param answers the {@link #answers} to set
	 */
	public void setAnswers(Set<Answer<P,O>> answers) {
		this.answers = answers;
	}
	
	/**
	 * Counts the answers of a specific type for this option
	 * @param type the answer type to count
	 * @return the count of answers of the given type
	 */
	public int countAnswers(AnswerType type) {
		int count = 0;
		for(Answer<P,O> answer : answers) {
			if (answer.getValue() == type) count++;
		}
		return count;
	}

	/* ========================
	 * = UI auxiliary methods =
	 * ======================== */
	
	public OptionListItem toOptionsListItem() {
		return new OptionListItem(this);
	}
	
	/* ========================
	 * = Other methods =
	 * ======================== */
	
	public boolean isPotentialWinnerByPositiveVotes() {
		List<O> aux = parent.getOptionsByPositiveAnswers();
		if (aux == null) return false; //TODO Maybe an exception?
		O best = aux.get(0);
		return OptionComparatorByPositiveVotes.GET.compare(this, best) <= 0;
	}
	
}
