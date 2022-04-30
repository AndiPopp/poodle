package de.andipopp.poodle.data.calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import biweekly.component.VEvent;
import biweekly.util.ICalDate;

/**
 * Wraps a {@link VEvent} and {@link ZoneId} to implement {@link CalendarEvent}.
 * @author Andi Popp
 *
 */
public class VEventWrapper implements CalendarEvent {

	/**
	 * The wrapped {@link VEvent}
	 */
	private final VEvent vEvent;
	
	/**
	 * The zone id; used in case the event represents an all day event
	 */
	private ZoneId zoneId;
	
	/**
	 * Construct new wrapper for given VEvent and time zone
	 * @param vEvent value for {@link #vEvent}
	 * @param zoneId value for {@link #zoneId}
	 */
	public VEventWrapper(VEvent vEvent, ZoneId zoneId) {
		this.vEvent = vEvent;
		this.zoneId = zoneId;
	}

	@Override
	public String getUid() {
		return vEvent.getUid().toString();
	}

	@Override
	public Date getStart() {
		return moveAllDayEventToTimeZone(vEvent.getDateStart().getValue());
	}

	@Override
	public Date getEnd() {
		return moveAllDayEventToTimeZone(vEvent.getDateEnd().getValue());
	}

	/**
	 * Checks whether an ICalDate is an all day event and translates it to {@link #zoneId} if necessary
	 * @param originalDate the original date
	 * @return the original date if it's {@link ICalDate#hasTime()} is true, the translated date otherwise
	 */
	public Date moveAllDayEventToTimeZone(ICalDate originalDate) {
		//if it is not an all day event, return the argument
		if (originalDate.hasTime()) return originalDate;
		
		//The original date is at 0:00 at the system's default time zone, build the local date
		LocalDateTime localDateTime = originalDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		//Now move the local date to this object's time zone
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}
	
	@Override
	public String getTitle() {
		return vEvent.getSummary().getValue();
	}

	@Override
	public String getLocation() {
		return vEvent.getLocation().getValue();
	}
	
}
