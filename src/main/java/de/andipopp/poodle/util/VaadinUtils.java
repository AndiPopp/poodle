package de.andipopp.poodle.util;

import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.server.VaadinRequest;

public class VaadinUtils {
	
	
	/**
	 * Turn an {@link Avatar} into an {@link AvatarGroupItem}
	 * @param avatar the {@link Avatar}
	 * @return the translated {@link AvatarGroupItem}
	 */
	public static AvatarGroupItem avatarToGroupItem(Avatar avatar) {
		AvatarGroupItem item = new AvatarGroupItem(avatar.getName());
		item.setColorIndex(avatar.getColorIndex());
		if (avatar.getImage() != null && !avatar.getImage().isEmpty()) item.setImage(avatar.getImage());
		else if(avatar.getImageResource() != null) item.setImageResource(avatar.getImageResource());
		return item;
	}
	
	public static boolean checkRequestForMobileBrowser(VaadinRequest request) {
		return request.getHeader("user-agent").contains("Mobile");
	}
	
	public static ZoneId guessTimeZoneFromVaadinRequest() {
		return new GregorianCalendar(VaadinRequest.getCurrent().getLocale()).getTimeZone().toZoneId();
	}
	
	public static Locale getLocaleFromVaadinRequest() {
		return VaadinRequest.getCurrent().getLocale();
	}
}
