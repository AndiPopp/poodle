/**
 * 
 */
package de.andipopp.poodle.data.entity.polls;

import javax.persistence.MappedSuperclass;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractEntity;

/**
 * An abstract representation of an option in a poll
 * @author Andi Popp
 */
@MappedSuperclass
public abstract class AbstractOption extends AbstractEntity implements Comparable<DateOption> {
	

	
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
