package de.andipopp.poodle.data.calendar;

import java.util.Date;
import java.util.UUID;

/**
 * A simple, straight-forward implementation of {@link CalendarEvent}
 * @author Andi Popp
 *
 */
public class SimpleCalendarEvent implements CalendarEvent {

	/**
	 * The UID
	 */
	String uid;
	
	/**
	 * Start date
	 */
	Date start;
	
	/**
	 * End date
	 */
	Date end;
	
	/**
	 * The title
	 */
	String title;
	
	/**
	 * The location
	 */
	String location;

	/**
	 * Empty constructor
	 */
	public SimpleCalendarEvent() {
		this.uid = UUID.randomUUID().toString();
	}
	
	/**
	 * Canonical constructor
	 * @param uid value for {@link #uid}
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 * @param title value for {@link #title}
	 * @param location value for {@link #location}
	 */
	public SimpleCalendarEvent(String uid, Date start, Date end, String title, String location) {
		this.uid = uid;
		this.start = start;
		this.end = end;
		this.title = title;
		this.location = location;
	}

	/**
	 * Copy constructor. Copies everything but the the {@link #uid}, which will be a random {@link UUID}
	 * @param event the event to be copied
	 */
	public SimpleCalendarEvent(CalendarEvent event) {
		this();
		this.setStart(event.getStart());
		this.setEnd(event.getEnd());
		this.setTitle(event.getTitle());
		this.setLocation(event.getLocation());
	}

	/**
	 * Getter for {@link #uid}
	 * @return the {@link #uid}
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Setter for {@link #uid}
	 * @param uid the {@link #uid} to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

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
	 * Getter for {@link #title}
	 * @return the {@link #title}
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter for {@link #title}
	 * @param title the {@link #title} to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
	
	

}
