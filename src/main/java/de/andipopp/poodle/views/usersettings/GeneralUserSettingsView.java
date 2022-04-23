package de.andipopp.poodle.views.usersettings;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class GeneralUserSettingsView extends VerticalLayout {

	TextField userName = new TextField("User Name");
	
	TextField displayName = new TextField("Display Name");
	
	PasswordField oldPassword = new PasswordField("Old Password");
	
	PasswordField newPassword = new PasswordField("New Password");
	
	PasswordField newPassword2 = new PasswordField("Retype New Password");
	
}
