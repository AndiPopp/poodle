package de.andipopp.poodle.data.entity.polls;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import biweekly.component.VEvent;

/**
 * An option in a {@link DatePoll}
 * @author Andi Popp
 *
 */
@Entity
public class DateOption extends AbstractOption<DatePoll> {

	/**
	 * The start date for this option
	 */
	private Date start;
	
	/**
	 * The end date for this option (can be null)
	 */
	private Date end;
	
	/**
	 * Specific location for this option (can be null)
	 */
	private String location;

	/* ================
	 * = Constructors =
	 * ================ */
	
	/**
	 * Empty constructor
	 */
	public DateOption() {}
	
	/**
	 * Full parameter constructor
	 * @param title value for {@link #getTitle()}
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 * @param location value for {@link #location}
	 */
	public DateOption(String title, @NotNull Date start, @NotNull Date end, String location) {
		super(title);
		setStart(start);
		setEnd(end);
		setLocation(location);
	}

	/**
	 * Construct new DateOption without title
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 * @param location value for {@link #location}
	 */
	public DateOption(@NotNull Date start, @NotNull Date end, String location) {
		this(null, start, end, location);
	}
	
	/**
	 * Construct new DateOption without title or location
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 */
	public DateOption(@NotNull Date start, @NotNull Date end) {
		this(null, start, end, null);
	}
	
	/* ======================
	 * = Getters and Setter =
	 * ====================== */

	/**
	 * Getter for {@link #start}
	 * @return the {@link #start}
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Setter for {@link #start}
	 * @param start the {@link #start} to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * Getter for {@link #end}
	 * @return the {@link #end}
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Setter for {@link #end}
	 * @param end the {@link #end} to set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * Getter for {@link #location}
	 * @return the {@link #location}
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Setter for {@link #location}
	 * @param location the {@link #location} to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public int compareTo(DateOption arg0) {
		//first check if UUID are the same
		if (this.getId().equals(arg0.getId())) return 0;
		
		//compare start date first
		int result = this.start.compareTo(arg0.start);
		if (result != 0) return result;
		//if equal, sort by end date
		result = this.end.compareTo(arg0.end);
		if (result != 0) return result;
		//if still equal, sort by location
		result = this.location.compareTo(arg0.location);
		if (result != 0) return result;
		//else sort by RUID
		return this.getId().compareTo(arg0.getId());
	}
	
	/**
	 * Construct a VEvent from this date option
	 * @return a VEvent constructed from this date option
	 */
	public VEvent toVEvent() {
		//new VEvent
		VEvent event = new VEvent();
		event.setCreated(new Date());
		//set start and end date
		if (getStart() != null) event.setDateStart(getStart());
		if (getEnd() != null) event.setDateEnd(getEnd());
		//set name of option as summary if given, else name of poll
		event.setSummary(getTitle());
		//return VEvent
		return event;
	}
	
	@Override
	public String toString() {
		
		//start with header:UUID and name
		String result = "DateOption" + getId().toString()  
				+ "\r\n" + "Title:" + getTitle();
		
		//add dates
		result += "\r\n" + "Start:" + start;
		result += "\r\n" + "End:" + end;
		
		if (location != null && location.length()>0) result += "\r\n" + "Location:" + location;
		
		//close with end line
		return result + "\r\n" + "EndOfDateOption";
	}
}