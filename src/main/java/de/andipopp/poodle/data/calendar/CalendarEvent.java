package de.andipopp.poodle.data.calendar;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
	public String getUid();
	
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
	
	static final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd MMM");
	
	static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	public default String format(ZoneId zoneId) {
		String result = getTitle();
		ZonedDateTime start = getStart().toInstant().atZone(zoneId);
		ZonedDateTime end = getEnd().toInstant().atZone(zoneId);
		
		//for all day events, tighten end by one day
		if (start.getHour() == 0 || end.getHour() == 0) end = end.minusDays(1);
		
		result += ", " + start.format(dayFormatter);
		if (start.getHour() != 0 || end.getHour() != 0) result += " " + start.format(timeFormatter);
		
		if (start.getDayOfMonth() != end.getDayOfMonth() || start.getHour() != 0 || end.getHour() != 0) 
			result += "–";

		if (start.getDayOfMonth() != end.getDayOfMonth()) result += end.format(dayFormatter) +" ";
		if (start.getHour() != 0 || end.getHour() != 0) result += end.format(timeFormatter);
		
		return result;
	}
	
	/**
	 * Check if two calendar events overlap
	 * @param event0 the first event
	 * @param event1 the second event
	 * @return true of the events overlap, false otherwise
	 */
	public static boolean overlap(CalendarEvent event0, CalendarEvent event1) {
		return (event0.getStart().getTime() > event1.getStart().getTime() 
				&& event0.getStart().getTime() < event1.getEnd().getTime())
			|| (event0.getEnd().getTime() > event1.getStart().getTime()
				&& event0.getStart().getTime() < event1.getEnd().getTime());
	}
	
	/**
	 * Check whether two events are within a specific time frame of each other
	 * @param event0 the first event
	 * @param event1 the second event
	 * @param seconds the length of the time frame in seconds
	 * @return true if the gap between the events is smaller than the time frame, false otherwise
	 */
	public static boolean within(CalendarEvent event0, CalendarEvent event1, long seconds) {
		//case event0 starts after event1
		if (event0.getStart().getTime() > event1.getStart().getTime()) {
			return event0.getStart().getTime() - event1.getEnd().getTime() <= seconds;
		//case event1 starts before or at the same time as event0
		} else {
			return event1.getStart().getTime() - event0.getEnd().getTime() <= seconds;
		}
	}
}
