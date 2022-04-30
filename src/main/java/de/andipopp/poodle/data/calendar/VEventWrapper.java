package de.andipopp.poodle.data.calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

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
		return moveIcalAllDayEventToTimeZone(vEvent.getDateStart().getValue());
	}

	@Override
	public Date getEnd() {
		return moveIcalAllDayEventToTimeZone(vEvent.getDateEnd().getValue());
	}

	private long getDuration() {
		return vEvent.getDateEnd().getValue().getTime() - vEvent.getDateStart().getValue().getTime();
	}
	
	/**
	 * Checks whether an ICalDate is an all day event and translates it to {@link #zoneId} if necessary
	 * @param originalDate the original date
	 * @return the original date if it's {@link ICalDate#hasTime()} is true, the translated date otherwise
	 */
	private Date moveIcalAllDayEventToTimeZone(ICalDate originalDate) {
		//if it is not an all day event, return the argument
		if (originalDate.hasTime()) return originalDate;
		//if not, move it to the specified time zone
		return moveFromDefaultTimeZone(originalDate);
	}
	
	private Date moveFromDefaultTimeZone(Date originalDate) {
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
	
	public class RecurrenceIterator implements Iterator<SimpleCalendarEvent> {

		Iterator<Date> startIterator;
	
		public RecurrenceIterator() {
			startIterator = vEvent.getDateIterator(TimeZone.getTimeZone(zoneId));
		}

		@Override
		public boolean hasNext() {
			return startIterator != null && startIterator.hasNext();
		}

		@Override
		public SimpleCalendarEvent next() {
			Date nextStart = startIterator.next();
			if (!vEvent.getDateStart().getValue().hasTime()) nextStart = moveFromDefaultTimeZone(nextStart);
			Date nextEnd = new Date(nextStart.getTime() + getDuration());
			return new SimpleCalendarEvent(getUid(), nextStart, nextEnd, getTitle(), getLocation());
		}
		
	}
}
