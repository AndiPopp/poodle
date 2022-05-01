package de.andipopp.poodle.data.calendar;

import java.time.ZoneId;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A class to gather scheduling conflicts for an event.
 * <p>The event for which the scheduling conflicts are gathered is the {@link #mainEvent}. Two types
 * of conflicts are considered.
 * <ul>
 * 	<li>A hard conflict is a {@link CalendarEvent} which overlaps with the {@link #mainEvent}. The
 * 		respective events are gathered within {@link #hardConflicts}.</li>
 *  <li>A soft conflict is a {@link CalendarEvent} which does not overlap with the {@link #mainEvent},
 *  	but is within a certain time before or after the {@link #mainEvent}. The time frame is defined
 *  	by {@link #softConflictMinutes}. The respective events are gathered within {@link #softConflicts}.</li>
 * </ul>
 * Events are mainly added by using {@link #checkConflict(CalendarEvent)}. The {@link #comparator} ensures
 * that events are not added multiple times.
 * </p>
 * @author Andi Popp
 *
 */
public class CalendarEventConflicts {

	/**
	 * Comparator to build {@link #hardConflicts} and {@link #softConflictMinutes}
	 */
	private static CalendarEventComparator comparator = new CalendarEventComparator(true);
	
	/**
	 * The event to which the conflicts have been gathered
	 */
	private CalendarEvent mainEvent;

	/**
	 * Minutes within which a time gap to the {@link #mainEvent} is considered a soft conflict
	 */
	private int softConflictMinutes;
	
	/**
	 * Events with a hard conflict, i.e. overlapping with {@link #mainEvent}
	 */
	private final SortedSet<CalendarEvent> hardConflicts = new TreeSet<>(comparator);
	
	/**
	 * Events with a soft conflict, 
	 */
	private final SortedSet<CalendarEvent> softConflicts = new TreeSet<>(comparator);

	/**
	 * Canonical constructor
	 * @param mainEvent value for {@link #mainEvent}
	 * @param softConflictMinutes value for {@link #softConflictMinutes}
	 */
	public CalendarEventConflicts(CalendarEvent mainEvent, int softConflictMinutes) {
		this.mainEvent = mainEvent;
		this.softConflictMinutes = softConflictMinutes;
	}
	
	/**
	 * Getter for {@link #mainEvent}
	 * @return the {@link #mainEvent}
	 */
	public CalendarEvent getMainEvent() {
		return mainEvent;
	}

	/**
	 * Getter for {@link #softConflictMinutes}
	 * @return the {@link #softConflictMinutes}
	 */
	public int getSoftConflictMinutes() {
		return softConflictMinutes;
	}

	/**
	 * Getter for {@link #hardConflicts}
	 * @return the {@link #hardConflicts}
	 */
	public SortedSet<CalendarEvent> getHardConflicts() {
		return hardConflicts;
	}

	/**
	 * Getter for {@link #softConflicts}
	 * @return the {@link #softConflicts}
	 */
	public SortedSet<CalendarEvent> getSoftConflicts() {
		return softConflicts;
	}

	/**
	 * Short-hand to check if this set of conflicts has hard conflicts
	 * @return true if we have hard conflicts
	 */
	public boolean hasHardConflicts() {
		return (hardConflicts != null && !hardConflicts.isEmpty());
	}
	
	/**
	 * Short-hand to check if this set of conflicts has soft conflicts
	 * @return true if we have soft conflicts
	 */
	public boolean hasSoftConflicts() {
		return (softConflicts != null && !softConflicts.isEmpty());
	}
	
	/**
	 * Sum of the size of both sets
	 * @return size of {@link #hardConflicts} + size of {@link #softConflicts}
	 */
	public int size() {
		return hardConflicts.size() + softConflicts.size();
	}
	
	/**
	 * Clear {@link #hardConflicts} and {@link #softConflicts}
	 */
	public void reset() {
		hardConflicts.clear();
		softConflicts.clear();
	}
	
	/**
	 * Checks if an event has a conflict an adds it to the respective set if it is.
	 * @param candidate the event to check
	 */
	public void checkConflict(CalendarEvent candidate) {
		//if the events overlap, we have a hard conflict
		if (CalendarEvent.overlap(mainEvent, candidate)) {
			hardConflicts.add(candidate);
		//if they do not overlap, but are within the tolerance, we have a soft conflict
		} else if (CalendarEvent.within(mainEvent, candidate, 60L * softConflictMinutes)) {
			softConflicts.add(candidate);
		}
	}
	
	/**
	 * Maximum number of iterator checks performed by {@link #checkConflicts(Iterator)},
	 * to ensure termination.
	 */
	private static final int MAX_ITERATOR_CHECKS = 999999;
	
	/**
	 * Iterates over several {@link CalendarEvent}s and checks them via {@link #checkConflict(CalendarEvent)}
	 * @param iterator the iterator defining the calendar events
	 * @throws TooManyCalendarEventsException if the number of events in the iterator exceeds {@link #MAX_ITERATOR_CHECKS}
	 */
	public void checkConflicts(Iterator<CalendarEvent> iterator) throws TooManyCalendarEventsException {
		int counter = 0;
		while(iterator.hasNext()) {
			if (counter > MAX_ITERATOR_CHECKS) throw new TooManyCalendarEventsException("Calendar events exceed limit (" +MAX_ITERATOR_CHECKS+").");
			checkConflict(iterator.next());
			counter++;
		}
	}
	
}
