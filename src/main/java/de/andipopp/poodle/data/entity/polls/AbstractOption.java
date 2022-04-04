/**
 * 
 */
package de.andipopp.poodle.data.entity.polls;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractEntity;

/**
 * An abstract representation of an option in a poll
 * @author Andi Popp
 */
@Entity
public abstract class AbstractOption<P extends AbstractPoll<? extends AbstractOption<P>>> extends AbstractEntity {
	
	@ManyToOne(targetEntity=AbstractPoll.class)
	private P parent;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Answer.class)
	private List<Answer<? extends AbstractOption<P>>> answers;
	
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
		
	}

	/**
	 * Construct a new Option with a given title
	 * @param title value for {@link #title}
	 */
	public AbstractOption(String title) {
		this();
		this.title = title;
	}
	
	/* ======================
	 * = Getters and Setter =
	 * ====================== */
	
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
	public List<Answer<? extends AbstractOption<P>>> getAnswers() {
		return answers;
	}

	/**
	 * Setter for {@link #answers}
	 * @param answers the {@link #answers} to set
	 */
	public void setAnswers(List<Answer<? extends AbstractOption<P>>> answers) {
		this.answers = answers;
	}

	
	
}
