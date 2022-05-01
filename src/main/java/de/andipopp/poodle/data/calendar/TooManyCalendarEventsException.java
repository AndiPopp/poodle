package de.andipopp.poodle.data.calendar;

public class TooManyCalendarEventsException extends Exception {

	public TooManyCalendarEventsException() {
		super();
	}

	public TooManyCalendarEventsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TooManyCalendarEventsException(String message) {
		super(message);
	}

	public TooManyCalendarEventsException(Throwable cause) {
		super(cause);
	}
}
