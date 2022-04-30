package de.andipopp.poodle.data.calendar;

import java.util.Date;

import biweekly.component.VEvent;

/**
 * Wraps a {@link VEvent} to implement {@link CalendarEvent}.
 * @author Andi Popp
 *
 */
public class VEventWrapper implements CalendarEvent {

	/**
	 * The wrapped {@link VEvent}
	 */
	private final VEvent vEvent;
	
	/**
	 * Construct new wrapper for given VEvent
	 * @param vEvent value for {@link #vEvent}
	 */
	public VEventWrapper(VEvent vEvent) {
		super();
		this.vEvent = vEvent;
	}

	@Override
	public String getUid() {
		return vEvent.getUid().toString();
	}

	@Override
	public Date getStart() {
		return vEvent.getDateStart().getValue();
	}

	@Override
	public Date getEnd() {
		return vEvent.getDateEnd().getValue();
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
