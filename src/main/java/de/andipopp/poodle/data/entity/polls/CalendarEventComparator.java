package de.andipopp.poodle.data.entity.polls;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

/**
 * A comparator for {@link CalendarEvent} objects.
 * It compares arg0 and arg1 in {@link #compare(CalendarEvent, CalendarEvent)} in the following steps.
 * <ul>
 * 	<li>Compare the start dates. If (only) one of them is null, this object will always be considered greater.</li>
 *  <li>Compare the end dates. If (only) one of them is null, this object will always be considered greater.</li>
 *  <li>Compare the titles as string, null values are treated as empty string.</li>
 *  <li>Compare the locations as string, null values are treated as empty string</li>
 * </ul>
 * 
 * <p>If result is still the same, the behavior deviates according to the {@link #ensureConsistentWithEquals} flag.</p>
 * 
 * <p>If set true, the comparator will compare the IDs and system identity codes next, ensuring only 
 * objects with the exact same fields and system properties return 0. This is useful if the comparator
 * is used for {@link SortedSet} or similar data structures. </p>
 * 
 * <p>If set to false, the comparator will return 0 after the first four steps. This is useful to keep
 * the order in case the comparator is used in sorting methods like {@link List#sort(Comparator)}.</p>
 * 
 * @author Andi Popp
 *
 */
public class CalendarEventComparator implements Comparator<CalendarEvent> {
	
	/**
	 * Make sure the comparator is consistent with equals as object
	 */
	private boolean ensureConsistentWithEquals = true;
	
	/**
	 * Construct a comparator with default settings
	 */
	public CalendarEventComparator() {}

	/**
	 * @param ensureConsistentWithEquals
	 */
	public CalendarEventComparator(boolean ensureConsistentWithEquals) {
		this();
		this.ensureConsistentWithEquals = ensureConsistentWithEquals;
	}

	@Override
	public int compare(CalendarEvent arg0, CalendarEvent arg1) {
		int result = 0;
		
		//first check if UUID are the same
		if (arg0.getUid() != null && arg0.getUid().equals(arg1.getUid())) return 0;
		
		//compare start date first, an event with a start date is always considered smaller than an event without
		if (arg0.getStart() != null && arg1.getStart() != null) {
			result = arg0.getStart().compareTo(arg1.getStart());
		} else if (arg0.getStart() != null && arg1.getStart() == null) {
			return -1;
		} else if (arg0.getStart() == null && arg1.getStart() != null) {
			return 1;
		}
		if (result != 0) return result;
		
		//if equal, sort by end date, an event with a start date is always considered smaller than an event without
		if (arg0.getEnd() != null && arg1.getEnd() != null) {
			result = arg0.getEnd().compareTo(arg1.getEnd());
		} else if (arg0.getEnd() != null && arg1.getEnd() == null) {
			return -1;
		} else if (arg0.getEnd() == null && arg1.getEnd() != null) {
			return 1;
		}
		if (result != 0) return result;
		
		//if still equal, sort by title, null is considered the same as empty string
		String arg0aux = "";
		String arg1aux = "";
		if (arg0.getTitle() != null) arg0aux = arg0.getTitle();
		if (arg1.getTitle() != null) arg0aux = arg1.getTitle();
		result = arg0aux.compareTo(arg1aux);
		if (result != 0) return result;
		
		//if still equal, sort by location
		arg0aux = "";
		arg1aux = "";
		if (arg0.getLocation() != null) arg0aux = arg0.getLocation();
		if (arg1.getLocation() != null) arg0aux = arg1.getLocation();
		result = arg0aux.compareTo(arg1aux);
		if (result != 0) return result;
		
		//if we don't need to ensure consistent with equals, we can stop here
		if (!ensureConsistentWithEquals) return result;

		//else sort by UUID, events with UUID are always considered smaller than events without
		if (arg0.getUid() != null && arg1.getUid() != null) {
			result = arg0.getUid().compareTo(arg1.getUid());
		} else if (arg0.getUid() != null && arg1.getUid() == null) {
			return -1;
		} else if (arg0.getUid() != null && arg1.getUid() == null) {
			return 1;
		}
		if (result != 0) return result;
		
		//if still equal, compare by system identity hashcode
		Integer h0 = System.identityHashCode(arg0);
		Integer h1 = System.identityHashCode(arg1);
		return h0.compareTo(h1);
	}
}
