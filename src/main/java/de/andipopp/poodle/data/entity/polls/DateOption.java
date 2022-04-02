package de.andipopp.poodle.data.entity.polls;

import java.util.Date;

import biweekly.component.VEvent;

/**
 * An option in a {@link DatePoll}
 * @author Andi Popp
 *
 */
public class DateOption extends AbstractOption {

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
}
