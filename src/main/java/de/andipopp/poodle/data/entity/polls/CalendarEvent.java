package de.andipopp.poodle.data.entity.polls;

import java.util.Date;
import java.util.UUID;

/**
 * An interface representing a calendar event
 * @author Andi Popp
 *
 */
public interface CalendarEvent {

	/**
	 * The event id
	 * @return the event id
	 */
	public UUID getUuid();
	
	/**
	 * Event start
	 * @return event start
	 */
	public Date getStart();
	
	/**
	 * Event end
	 * @return event end
	 */
	public Date getEnd();
	
	/**
	 * Event title
	 * @return the event title
	 */
	public String getTitle();
	
	/**
	 * Event location
	 * @return the event location
	 */
	public String getLocation();
	
}
