/**
 * 
 */
package de.andipopp.poodle.data.entity.polls;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractEntity;

/**
 * An abstract representation of an option in a poll
 * @author Andi Popp
 */
@Entity
public abstract class AbstractOption<P extends AbstractPoll<?>> extends AbstractEntity implements Comparable<DateOption> {
	
	@ManyToOne(targetEntity=AbstractPoll.class)
	private P parent;
	
	/**
	 * An optional human readable title
	 */
	private String title;

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

	
}
