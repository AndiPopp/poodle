package de.andipopp.poodle.data.calendar;

import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import biweekly.ICalendar;
import biweekly.component.VEvent;

/**
 * A wrapper for an {@link ICalendarWrapper} to produce an {@link ICalendarWrapperEventIterator}.
 * @author Andi Popp
 *
 */
public class ICalendarWrapper {

	/**
	 * The wrapped iCalendar
	 */
	ICalendar iCalendar;
	
	/**
	 * The maximum date until which to iterate
	 */
	Date maxDate;
	
	/**
	 * The time zone in which to locate all day events and with which to consider day-light saving time
	 */
	ZoneId zoneId;
	
	
	/**
	 * Canonical constructor
	 * @param iCalendar value for {@link #iCalendar}
	 * @param maxDate value for {@link #maxDate}
	 * @param zoneId value for {@link #zoneId}
	 */
	public ICalendarWrapper(ICalendar iCalendar, Date maxDate, ZoneId zoneId) {
		this.iCalendar = iCalendar;
		this.maxDate = maxDate;
		this.zoneId = zoneId;
	}

	/**
	 * Iterator defined by an {@link ICalendarWrapper}.
	 * <p>This class iterates over the {@link VEvent}s in the parent {@link ICalendarWrapper#iCalendar},
	 * producing single {@link SimpleCalendarEvent}s for each occurrence of recurring {@link VEvent}s.
	 * This is achieved by wrapping to types of {@link Iterator}s.</p>
	 * <p>The {@link #mainIterator} iterates through the actual VEvents, while the {@link #subIterator}
	 * produces {@link SimpleCalendarEvent} version of each occurrence of the event until the 
	 * {@link ICalendarWrapper#maxDate}. To cap recurring events at the max date, this iterator peaks
	 * ahead and remembers the {@link #cachedNext}.</p>
	 * @author Andi Popp
	 *
	 */
	public class ICalendarWrapperEventIterator implements Iterator<CalendarEvent> {

		/**
		 * The main iterator iterating over the {@link VEvent}s in the {@link #ICalendarWrapper()}'s {@link ICalendar}
		 */
		Iterator<VEvent> mainIterator;
		
		/**
		 * Sub-iterator for recurring events.
		 * It is assumed that events recur in order in the sub iterator 
		 */
		Iterator<? extends SimpleCalendarEvent> subIterator;
		
		/**
		 * The next element of this iterator 
		 */
		CalendarEvent cachedNext;
		
		/**
		 * Construct new iterator
		 */
		public ICalendarWrapperEventIterator() {
			mainIterator = iCalendar.getComponents(VEvent.class).iterator();
			subIterator = null;
			cacheNext();
		}

		@Override
		public boolean hasNext() {
			return cachedNext != null;
		}

		@Override
		public CalendarEvent next() throws NoSuchElementException {
			if (cachedNext == null) throw new NoSuchElementException();
			
			CalendarEvent next = cachedNext;
			cacheNext();
			return next;
		}
		
		/**
		 * Cache the next element
		 */
		private void cacheNext() {
			cachedNext = null; //default to null
			
			//first check the sub-iterator
			if(subIterator != null && !subIterator.hasNext()) {
				//we are done with the sub iterator, set it to null
				subIterator = null;
			} else if (subIterator != null) {
				//check if the next element in the sub-iterator is within the date range
				CalendarEvent potentialNext = subIterator.next();
				if (potentialNext.getStart().getTime() < maxDate.getTime()) {
					cachedNext = potentialNext;
					return;
				} else {
					//we skip the remaining 
					subIterator = null;
				}
			}
			
			//if we make it this far, we check the main iterator
			//if it's empty, we stop here 
			if (!mainIterator.hasNext()) return; 
			//if not, we pull the next VEvent and call the method again
			VEventWrapper nextVEventWrapper = new VEventWrapper(mainIterator.next(), zoneId);
			subIterator = nextVEventWrapper.new RecurrenceIterator();
			cacheNext();
		}
		
	}
}
