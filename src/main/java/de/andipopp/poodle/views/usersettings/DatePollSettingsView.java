package de.andipopp.poodle.views.usersettings;

import java.time.ZoneId;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.InvalidException;
import de.andipopp.poodle.views.components.TextFieldList;
import de.andipopp.poodle.views.components.ZoneIdComboBox;

@PermitAll
public class DatePollSettingsView extends VerticalLayout {

	//UI Components
	
	ZoneIdComboBox timeZonePicker;
	
	IntegerField softConflictMinutes = new IntegerField("Time Frame for Soft Conflicts (Minutes)");
	
	TextFieldList icsPaths;
	
	Button saveButton = new Button("Save");
	
	//Beans and Services
	
	private final AuthenticatedUser authenticatedUser;
	
	private User currentUser;
	
	private UserService userService;
	
	private Binder<User> binder = new BeanValidationBinder<>(User.class);
	
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
		timeZonePicker.setMinWidth("20em");
		if (currentUser.getZoneId() != null && !currentUser.getZoneId().isBlank()) timeZonePicker.setValue(ZoneId.of(currentUser.getZoneId()));

		softConflictMinutes.setHasControls(true);
		
		FormLayout generalSettings = new FormLayout(timeZonePicker, softConflictMinutes);
		
		icsPaths = new TextFieldList();
		icsPaths.setPadding(false);
		
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> saveUser());
		
		this.setPadding(false);
		this.setSpacing(false); //spacing with heading
		
		H4 firstHeading = new H4("General Date Poll Settings");
		firstHeading.getStyle().set("margin-top", "0px");
		
		HorizontalLayout buttonBar = new HorizontalLayout(saveButton);
		buttonBar.setWidthFull();
		buttonBar.setJustifyContentMode(JustifyContentMode.END);
		
		if (mayEdit()) {
			this.add(
//				firstHeading,
				generalSettings, 
				new H4("ICS Paths for Conflict Warning"), 
				icsPaths,
				new Paragraph(),
				buttonBar
			);
		}else {
			this.add(new Label("Access denied"));
		}
		
		configureBinder();
		
	}
	
	private void saveUser() {
		if (!mayEdit()) {
			Notification.show("Access denied").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		try {
			binder.writeBean(currentUser);
		} catch (ValidationException e) {
			Notification.show("Please fix missing entries first.").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		try {
			currentUser = userService.update(currentUser);
		} catch (InvalidException e) {
			Notification.show("Error while saving user data: "+e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		Notification.show("Saved", 3000, Position.BOTTOM_CENTER) .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	}

	private void configureBinder() {
		binder.forField(icsPaths).bind("icsPaths");
		binder.forField(timeZonePicker)
			.withConverter(new ZoneIdConverter())
			.bind("zoneId");
		binder.bindInstanceFields(this);
		binder.readBean(currentUser); 
		icsPaths.update();
	}
	
	private boolean mayEdit() {
		return (authenticatedUser != null && !authenticatedUser.get().isEmpty() && (
			authenticatedUser.get().get().getRoles().contains(Role.ADMIN) ||
			authenticatedUser.get().get().equals(currentUser)
		));
	}
	
	
}
