package de.andipopp.poodle.views.usersettings;

import java.time.ZoneId;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.views.components.TextFieldList;
import de.andipopp.poodle.views.components.ZoneIdComboBox;

public class DatePollSettingsView extends VerticalLayout {

	ZoneIdComboBox timeZonePicker;
	
	TextFieldList icsPaths;
	
	private final AuthenticatedUser authenticatedUser;
	
	private User currentUser;
	
	private UserService userService;

	/**
	 * @param authenticatedUser
	 * @param currentUser
	 * @param userService
	 */
	public DatePollSettingsView(AuthenticatedUser authenticatedUser, User currentUser, UserService userService) {
		this.authenticatedUser = authenticatedUser;
		this.currentUser = currentUser;
		this.userService = userService;
		
		timeZonePicker = new ZoneIdComboBox();
		if (currentUser.getZoneId() != null && !currentUser.getZoneId().isBlank()) timeZonePicker.setValue(ZoneId.of(currentUser.getZoneId()));

		icsPaths = new TextFieldList(currentUser.getIcsPaths());
		
	}
	
	
}
