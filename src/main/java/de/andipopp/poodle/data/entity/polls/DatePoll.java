package de.andipopp.poodle.data.entity.polls;

import java.util.Iterator;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.PoodleApplication;

@Entity
public class DatePoll extends AbstractPoll<DatePoll, DateOption> {

	/**
	 * The polls time zone
	 */
	@NotNull
	private TimeZone timeZone;
	
	/**
	 * An optional location
	 */
	private String location;
	
	/**
	 * Construct new poll with random RUID
	 */
	public DatePoll() {
		this.setTitle("Choose a title...");
		this.timeZone = TimeZone.getDefault();
	}

	/**
	 * @param title
	 * @param description
	 * @param timeZone
	 * @param location
	 */
	public DatePoll(@NotNull String title, String description, @NotNull TimeZone timeZone, String location) {
		super(title, description);
		this.timeZone = timeZone;
		this.location = location;
	}

	/**
	 * Getter for {@link #timeZone}
	 * @return the {@link #timeZone}
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * Setter for {@link #timeZone}
	 * @param timeZone the {@link #timeZone} to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	/**
	 * Getter for {@link #location}
	 * @return the {@link #location}
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Setter for {@link #location}. Cleans dangerous HTML.
	 * @param location the {@link #location} to set
	 */
	public void setLocation(String location) {
		this.location = Jsoup.clean(location, Safelist.basic());
	}

	/**
	 * Build the product ID for this polls iCal product ID
	 * @return the product ID for this polls iCal product ID
	 */
	public String iCalProductID() {
		return "-//" +PoodleApplication.nameAndVersion() +  "//" + getTitle() +  "//EN"; //TODO Language support
	}
	
	@Override
	protected void setParent(DateOption option) {
		option.setParent(this);
	}

	public String metaInf() {
		//First line is always the type identifier and the UUID
		String result = "DatePoll@" + getId().toString();
		//add the name
		if (getTitle() != null) result += "\r\nTitle: " + getTitle();
		//add location
		if (getLocation() != null) result += "\r\nLocation: "  + getLocation();
		//add time zone
		if (timeZone != null) result += "\r\nTimeZome: " + timeZone.getID();
		
		return result;
	}
	
	@Override
	public String toString() {
		//start with the meta info
		String result = metaInf();
		//add the strings for the options
		for (Iterator<DateOption> it = getOptionIterator(); it.hasNext(); ) {
			result += "\r\n" + it.next().toString();
		}
//		for (DateOption option : getOptions()) {
//		result += "\r\n" + option.toString();
//		}
		//add winners if present
		Iterator<DateOption> it = getWinnerIterator();
		if (it != null) {
			result += "\r\n" + "Winners: ";
			String sep = "";
			while(it.hasNext()) {
				result += sep + it.next().getId();
				sep = ",";
			}
		}
		
		//return result
		return result;
	}
}
