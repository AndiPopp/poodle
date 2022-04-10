/**
 * 
 */
package de.andipopp.poodle.data.entity.polls;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractAutoIdEntity;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.views.poll.OptionListItem;

/**
 * An abstract representation of an option in a poll
 * @author Andi Popp
 */
@Entity(name = "Option")
public abstract class AbstractOption<P extends AbstractPoll<P,O>, O extends AbstractOption<P,O>> extends AbstractAutoIdEntity {	

	@ManyToOne(targetEntity=AbstractPoll.class)
	private P parent;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Answer.class, mappedBy = "option", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Answer<P,O>> answers;
	
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
		this.answers = new ArrayList<>();
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
	
	public void removeAnswer(Answer<P, O> answer) {
		answers.remove(answer);
		answer.setOption(null);
	}
	
	public void removeAnswer(Vote<P,O> vote) {
		List<Answer<P,O>> deleteThose = new ArrayList<>(1); //should typically only one answer per option
		for(Answer<P, O> answer : answers) {
			if (answer.getVote().equals(vote)) {
				answer.setOption(null);
				deleteThose.add(answer);
			}
		}
		answers.removeAll(deleteThose);
		
	}
	
	public void addAnswer(Answer<P, O> answer) {
		answers.add(answer);
		setThisOption(answer);
	}
	
	protected abstract void setThisOption(Answer<P, O> answer);

	/**
	 * Counts the answers of a specific type for this option
	 * @param type the answer type to count
	 * @return the count of answers of the given type
	 */
	public int countAnswers(AnswerType type) {
		if (answers == null) return -1;
		int count = 0;
		for(Answer<P,O> answer : answers) {
			if (type == null || answer.getValue() == type) count ++;
		}
		return count;
	}
	
	/* ========================
	 * = UI auxiliary methods =
	 * ======================== */
	
	public OptionListItem toOptionsListItem(User currentUser) {
		return new OptionListItem(this, currentUser);
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
