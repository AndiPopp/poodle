package de.andipopp.poodle.data.entity.polls;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import biweekly.component.VEvent;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.util.JSoupUtils;
import de.andipopp.poodle.views.vote.date.DateOptionListItem;

/**
 * An option in a {@link DatePoll}
 * @author Andi Popp
 *
 */
@Entity
public class DateOption extends AbstractOption<DatePoll, DateOption> implements CalendarEvent {

	/**
	 * The start date for this option
	 */
	private Date start;
	
	/**
	 * The end date for this option (can be null)
	 */
	private Date end;
	
	/**
	 * Max length for {@link #location}
	 */
	public static final int MAX_LOCATION_LENGTH = 63;
	
	/**
	 * Specific location for this option (can be null)
	 */
	@Nullable
	@Column(length = MAX_LOCATION_LENGTH)
	@Size(max = MAX_LOCATION_LENGTH)
	private String location;

	/* ================
	 * = Constructors =
	 * ================ */
	
	/**
	 * Empty constructor
	 */
	public DateOption() {}
	
	/**
	 * Full parameter constructor
	 * @param title value for {@link #getTitle()}
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 * @param location value for {@link #location}
	 */
	public DateOption(String title, @NotNull Date start, @NotNull Date end, String location) {
		super(title);
		setStart(start);
		setEnd(end);
		setLocation(location);
	}

	/**
	 * Construct new DateOption without title
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 * @param location value for {@link #location}
	 */
	public DateOption(@NotNull Date start, @NotNull Date end, String location) {
		this(null, start, end, location);
	}
	
	/**
	 * Construct new DateOption without title or location
	 * @param start value for {@link #start}
	 * @param end value for {@link #end}
	 */
	public DateOption(@NotNull Date start, @NotNull Date end) {
		this(null, start, end, null);
	}
	
	/**
	 * Construct new DateOption without title or location with local date timess and a time zone
	 * @param start local date-time for {@link #start}
	 * @param end local date-time for {@link #end}
	 * @param timeZone the time zone to translate the local date-times to instants
	 */
	public DateOption(@NotNull LocalDateTime start, @NotNull LocalDateTime end, @NotNull ZoneId timeZone) {
		this(
			Date.from(start.atZone(timeZone).toInstant()),
			Date.from(end.atZone(timeZone).toInstant())
		);
	}
	
	/* ======================
	 * = Getters and Setter =
	 * ====================== */

	@Override
	protected void setThisOption(Answer<DatePoll, DateOption> answer) {
		answer.setOption(this);
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
	
	/* ========================
	 * = UI auxiliary mehtods =
	 * ======================== */
	
	@Override
	public DateOptionListItem toOptionsListItem(User currentUser) {
		return new DateOptionListItem(this, currentUser);
	}
	
	/* ===============
	 * = Conversions =
	 * =============== */
	
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	private static DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");
	
	private static String weekdayFormat = ("E");
	
	public ZonedDateTime getZonedStart(ZoneId zoneId) {
		return ZonedDateTime.ofInstant(start.toInstant(), zoneId);
	}
	
	public ZonedDateTime getZonedEnd(ZoneId zoneId) {
		return ZonedDateTime.ofInstant(end.toInstant(), zoneId);
	}
	
	public String getZonedTimeStartEnd(ZoneId zoneId) {
		return timeFormatter.format(getZonedStart(zoneId)) + " - " + timeFormatter.format(getZonedEnd(zoneId));
	}
	
	public String getZonedStartDay(ZoneId zoneId) {
		return dayFormatter.format(getZonedStart(zoneId));
	}
	
	public String getZonedStartWeekday(ZoneId zoneId, Locale locale) {
		if (locale == null) locale = Locale.getDefault();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(weekdayFormat, locale);
		return formatter.format(getZonedStart(zoneId));
	}
	
	public long getZonedDayJumps(ZoneId zoneId) {
		//a duration object to calculate the full 24h days between the dates
		Duration duration = Duration.between(start.toInstant(), end.toInstant());
		//an auxiliary day if there is a non-full date jump in the zone
		int aux = 0;
		ZonedDateTime zonedStart = getZonedStart(zoneId);
		ZonedDateTime zonedEnd = getZonedEnd(zoneId);
		int startDayMinute = zonedStart.getHour()*60 + zonedStart.getMinute();
		int endDayMinute = zonedEnd.getHour()*60 + zonedEnd.getMinute();
		if (startDayMinute > endDayMinute) aux = 1;
		return aux + duration.toDays();

	}
	
	/* ========================
	 * = Other methods =
	 * ======================== */
	
	/**
	 * Construct a VEvent from this date option
	 * @return a VEvent constructed from this date option
	 */
	public VEvent toVEvent() {
		//new VEvent
		VEvent event = new VEvent();
		event.setCreated(new Date());
		//set start and end date
		if (getStart() != null) event.setDateStart(getStart()); //should always be != null
		if (getEnd() != null) event.setDateEnd(getEnd()); //should always be != null
		//set the UUID
		event.setUid(getId() + "@poodle");
		//set poll title as summary 
		if (getParent().getTitle() != null) event.setSummary(getParent().getTitle()); //should always be != null
		//build description from poll description, option title and answers
		event.setDescription(buildIcalDescription());
		
		//return VEvent
		return event;
	}
	
	private String buildIcalDescription() {
		String desc = "";
		if (getParent().getDescription() != null && !getParent().getDescription().isBlank()) {
			desc += JSoupUtils.cleanNone(getParent().getDescription()) + "\n\n";
		}
		if (this.getTitle() != null && !this.getTitle().isBlank()) {
			desc += JSoupUtils.cleanNone(getTitle())  + "\n\n";
		}
		
		sortAnswersByDisplayName();
		String listByType = listDisplayNamesByAnswer(AnswerType.YES, "* ");
		if (listByType != null && !listByType.isBlank()) desc += "Available:\n" +listByType;
		listByType = listDisplayNamesByAnswer(AnswerType.IF_NEED_BE, "* ");
		if (listByType != null && !listByType.isBlank()) desc += "Available if need be:\n" +listByType;
		listByType = listDisplayNamesByAnswer(AnswerType.NO, "* ");
		if (listByType != null && !listByType.isBlank()) desc += "Not available:\n" +listByType;
		
		return desc;
	}
	
	@Override
	public String toString() {
		
		//start with header:UUID and name
		String result = "DateOption" + getId().toString()  
				+ "\r\n" + "Title:" + getTitle();
		
		//add dates
		result += "\r\n" + "Start:" + start;
		result += "\r\n" + "End:" + end;
		
		if (location != null && location.length()>0) result += "\r\n" + "Location:" + location;
		
		//close with end line
		return result + "\r\n" + "EndOfDateOption";
	}

	public DateOption clone(int dayOffSet) {
		try {
			return new DateOption(
					this.getTitle(), //Title
					Date.from(getStart().toInstant().plusSeconds(dayOffSet*24*60*60)), //Start
					Date.from(getEnd().toInstant().plusSeconds(dayOffSet*24*60*60)), //End
					this.getLocation() //Location
			);
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public UUID getUuid() {
		return getId();
	}
	
}
