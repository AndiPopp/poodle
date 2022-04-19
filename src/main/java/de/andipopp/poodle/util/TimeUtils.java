package de.andipopp.poodle.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import de.andipopp.poodle.data.entity.User;

public class TimeUtils {
	
	public static SortedSet<TimeZone> getSupportedTimeZonesByOffset(boolean includeEtcs) {
		SortedSet<TimeZone> set = new TreeSet<>((arg0, arg1) ->  {
			int diffOffset = arg0.getRawOffset() - arg1.getRawOffset();
			if (diffOffset != 0) return diffOffset;
			return arg0.getID().compareTo(arg1.getID());
		});
		
		for(String id :TimeZone.getAvailableIDs()) {
			if (includeEtcs || !id.startsWith("Etc")) set.add(TimeZone.getTimeZone(id));
		}
		return set;
	}
	
	public static List<ZoneId> getSupportedZoneIdsInAlphabeticalOrder() {
		SortedSet<String> ids = new TreeSet<String>();
		ids.addAll(ZoneId.getAvailableZoneIds());
		
		List<ZoneId> zoneIds = new ArrayList<>(ids.size());
		for(String id : ids) {
			zoneIds.add(ZoneId.of(id));
		}
		return zoneIds;
	}
	
	public static DateTimePicker updateDateTimePicker(DateTimePicker picker, ZoneId oldTimeZone, ZoneId newTimeZone) {
		if (picker == null) return null;
		ZonedDateTime current = picker.getValue().atZone(oldTimeZone);
		picker.setValue(current.withZoneSameInstant(newTimeZone).toLocalDateTime());
		return picker;
	}
	
	public static LocalDateTime setDateTimePicker(DateTimePicker picker, Instant timestamp, ZoneId timeZone) {
		if (picker == null || timestamp == null || timeZone == null) return null;
		LocalDateTime local = ZonedDateTime.ofInstant(timestamp, timeZone).toLocalDateTime();
		picker.setValue(local);
		return local;
	}
	
	public static final String TIME_ZONE_COOKIE_NAME = "TimeZone";
	
	public static ZoneId getUserTimeZone(User user) {
		//first check if we can get the time zone from the user settings
		if (user != null && user.getTimeZone() != null) return user.getTimeZone();
		
		//if not, see if we have a time zone cookie
		for(Cookie cookie : VaadinRequest.getCurrent().getCookies()) {
			if (cookie.getName().equals(TIME_ZONE_COOKIE_NAME)) return ZoneId.of(cookie.getValue());
		}
		
		//if everything else fails, we guess from the Locale
		return VaadinUtils.guessTimeZoneFromVaadinRequest();
	}
	
	public static void rememberTimeZoneInCookie(ZoneId timeZone) {
		ResponseCookie timeZoneCookie = ResponseCookie.from(TIME_ZONE_COOKIE_NAME, timeZone.getId())
			.httpOnly(true)
			.maxAge(Integer.MAX_VALUE)
			.sameSite("Lax")
			.build();
		VaadinService.getCurrentResponse().setHeader(HttpHeaders.SET_COOKIE, timeZoneCookie.toString());
	}
}
