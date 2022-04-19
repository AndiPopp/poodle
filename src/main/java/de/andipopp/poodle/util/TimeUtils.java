package de.andipopp.poodle.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import de.andipopp.poodle.data.entity.User;

/**
 * Class containining static methods for convenient date, time and time zone handling.
 * @author Andi Popp
 *
 */
public class TimeUtils {
	
	/**
	 * Get all zone IDs from {@link ZoneId#getAvailableZoneIds()}, but sorted alphabetically
	 * @return all zone IDs from {@link ZoneId#getAvailableZoneIds()} sorted alphabetically
	 */
	public static List<ZoneId> getSupportedZoneIdsInAlphabeticalOrder() {
		SortedSet<String> ids = new TreeSet<String>();
		ids.addAll(ZoneId.getAvailableZoneIds());
		
		List<ZoneId> zoneIds = new ArrayList<>(ids.size());
		for(String id : ids) {
			zoneIds.add(ZoneId.of(id));
		}
		return zoneIds;
	}
	
	/**
	 * Update a {@link DateTimePicker} from one time zone to another, keeping the logical time stamp.
	 * {@link DatePicker}s have {@link LocalDateTime} values which need to be combined with an 
	 * external {@link ZoneId} to be translated into {@link Date}s/{@link Instant}s. This method
	 * will translate the {@link DatePicker}'s {@link LocalDateTime} value so that the new value represents
	 * the same {@link Instant} at the new time zone as the old values does at the old time zone.
	 * @param picker the picker to update
	 * @param oldTimeZone the old time zone, i.e. the time zone associated with the current value of the picker
	 * @param newTimeZone the new time zone, i.e. the time zone the picker shall be translated to
	 * @return the picker argument for convenience purposes
	 */
	public static DateTimePicker updateDateTimePicker(DateTimePicker picker, ZoneId oldTimeZone, ZoneId newTimeZone) {
		if (picker == null) return null;
		ZonedDateTime current = picker.getValue().atZone(oldTimeZone);
		picker.setValue(current.withZoneSameInstant(newTimeZone).toLocalDateTime());
		return picker;
	}
	
	/**
	 * Set a {@link DateTimePicker} to a {@link LocalDateTime} represented by the arguments.
	 * {@link DatePicker}s have {@link LocalDateTime} values which can represent a global date-time at
	 * an externally specified zone id, represented by an {@link Instant} and a {@link ZoneId}.
	 * This method takes two argument of these types, translates them to a {@link LocalDateTime} and
	 * sets the {@link DateTimePicker} argument to the calculated value.
	 * @param picker the picker to update
	 * @param timestamp the global time stamp to be evaluated at the given time zone
	 * @param timeZone the time zone at which to evaluate the timestamp at
	 * @return the calculated {@link LocalDateTime}, i.e. the new value of the picker
	 */
	public static LocalDateTime setDateTimePicker(DateTimePicker picker, Instant timestamp, ZoneId timeZone) {
		if (picker == null || timestamp == null || timeZone == null) return null;
		LocalDateTime local = ZonedDateTime.ofInstant(timestamp, timeZone).toLocalDateTime();
		picker.setValue(local);
		return local;
	}
	
	/**
	 * Name of the time zone cookie
	 */
	private static final String TIME_ZONE_COOKIE_NAME = "TimeZone";
	
	/**
	 * Get a viable time zone for the user.
	 * The method works in four steps.
	 * <ol>
	 *  <li>If the user argument is not null and the user has a non-null {@link User#getTimeZone()}, the method returns it.</li>
	 *  <li>Otherwise, if the session has an attribute named {@value #TIME_ZONE_COOKIE_NAME}, we construct the time zone from its value.</li>
	 *  <li>Otherwise, if there is a specific cookie named {@value #TIME_ZONE_COOKIE_NAME}, we construct the time zone from its value.</li>
	 *  <li>Otherwise, we guess the time zone via {@link VaadinUtils#guessTimeZoneFromVaadinRequest()}.</li>
	 * </ol>
	 * @param user the user object, can be null
	 * @return the time zone id determined by this method 
	 */
	public static ZoneId getUserTimeZone(@Nullable User user) {
		//first check if we can get the time zone from the user settings
		if (user != null && user.getTimeZone() != null) return user.getTimeZone();
		
		//if not, see if the session knows anything
		if (VaadinRequest.getCurrent().getWrappedSession().getAttributeNames().contains(TIME_ZONE_COOKIE_NAME)) {
			return ZoneId.of(VaadinRequest.getCurrent().getWrappedSession().getAttribute(TIME_ZONE_COOKIE_NAME).toString());
		}
		
		//if not, see if we have a time zone cookie
		for(Cookie cookie : VaadinRequest.getCurrent().getCookies()) {
			if (cookie.getName().equals(TIME_ZONE_COOKIE_NAME)) return ZoneId.of(cookie.getValue());
		}
		
		//if everything else fails, we guess from the Locale
		return VaadinUtils.guessTimeZoneFromVaadinRequest();
	}
	
	/**
	 * Remember the time zone in a cookie.
	 * This method does a two-fold approach: Store the time zone id in the session and in a specific cookie.
	 * The session will have precedence over the specific cookie, the latter will function as fallback.
	 * @param zoneId the zone id to remember
	 */
	public static void rememberZoneIdInCookie(ZoneId zoneId) {
		VaadinRequest.getCurrent().getWrappedSession().setAttribute(TIME_ZONE_COOKIE_NAME, zoneId.getId());
		ResponseCookie timeZoneCookie = ResponseCookie.from(TIME_ZONE_COOKIE_NAME, zoneId.getId())
			.httpOnly(true)
			.maxAge(Integer.MAX_VALUE)
			.sameSite("Lax")
			.build();
		VaadinService.getCurrentResponse().setHeader(HttpHeaders.SET_COOKIE, timeZoneCookie.toString());
	}
}
