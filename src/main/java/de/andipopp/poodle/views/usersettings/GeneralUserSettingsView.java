package de.andipopp.poodle.views.usersettings;

import java.util.Date;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.InvalidException;
import de.andipopp.poodle.views.components.ImageUpload;
import de.andipopp.poodle.views.components.ImageUpload.ImageReceiver;

public class GeneralUserSettingsView extends VerticalLayout {

	private TextField userName = new TextField("User Name");
	
	private TextField displayName = new TextField("Display Name");
	
	private PasswordField oldPassword = new PasswordField("Old Password");
	
	private PasswordField newPassword = new PasswordField("New Password");
	
	private PasswordField newPassword2 = new PasswordField("Retype New Password");

	private HorizontalLayout avatarPanel = new HorizontalLayout();
	
	private Avatar currentAvatar;
	
	private ImageUpload imageUpload;
	
	private Button save = new Button("Save");
	
	private Binder<User> binder = new BeanValidationBinder<>(User.class);

	private Binder<User> pwBinder = new Binder<>(User.class);
	
	private User user;

	private final AuthenticatedUser authenticatedUser;
	
	private final UserService userService;

	public GeneralUserSettingsView(User user, AuthenticatedUser authenticatedUser, UserService userService) {
		this.user = user;
		this.authenticatedUser = authenticatedUser;
		this.userService = userService;
		configureBinder();
		
		this.setPadding(false);
		this.setSpacing(false);
		
		FormLayout names = new FormLayout(userName, displayName);
		userName.setEnabled(UserSettingsView.isAdmin(authenticatedUser));
		
		FormLayout password = new FormLayout(oldPassword, new Label(), newPassword,  newPassword2);
		
		imageUpload = new ImageUpload(new ImageReceiver(Config.getCurrent().getUserImagePath(), user.getId()));
		imageUpload.addSucceededListener(e -> {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			imageUpload.clearFileList();
			currentAvatar.setImage(currentAvatar.getImage() + "&now="+(new Date()).getTime()); //just at some random useless param to trigger a reload
		});
		avatarPanel.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		configureAvatarPanel();
		
		HorizontalLayout buttonPanel = new HorizontalLayout(save);
		buttonPanel.setWidthFull();
		buttonPanel.setJustifyContentMode(JustifyContentMode.END);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickListener(e -> prepareSaveUser());
		
		H4 firstHeader = new H4("Names");
		firstHeader.getStyle().set("margin-top", "0px");
		
		this.add(
			firstHeader,
			names,
			new H4("Change Password"),
			password,
			new Paragraph(),
			buttonPanel,
			new H4("Change Avarar"),
			avatarPanel
		);
		
	}

	private void configureBinder() {
		binder.bindInstanceFields(this);
		binder.readBean(this.user);
		pwBinder.forField(newPassword)
			.withValidator(password -> password.length() >= Config.getCurrent().getMinPasswordLength(), "Password must be at least "+Config.getCurrent().getMinPasswordLength()+" characters")
			.withConverter(new PasswordHashConverter())
			.bind("hashedPassword");
		pwBinder.forField(newPassword2)
			.withValidator(password -> password.equals(newPassword.getValue()), "Passwords do not match")
			.withConverter(new PasswordHashConverter())
			.bind("hashedPassword2");
	}

	private void prepareSaveUser() {
		if (authenticatedUser == null || authenticatedUser.get().isEmpty() || !(user.equals(authenticatedUser.get().get()) || UserSettingsView.isAdmin(authenticatedUser))) {
			Notification.show("Access denied").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (!binder.validate().isOk() || (!newPassword.isEmpty() && !pwBinder.validate().isOk())) {
			Notification.show("Please fix invalid fields").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (!newPassword.isEmpty() && !PasswordHashConverter.BCRYPT_PASSWORD_ENCODER.matches(oldPassword.getValue(), user.getHashedPassword())) {
			Notification.show("Old password is invalid").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		binder.writeBeanIfValid(user);
		if (!newPassword.isEmpty()) {
			pwBinder.writeBeanIfValid(user);
			oldPassword.clear();
			newPassword.clear();
			newPassword2.clear();
		}
		
		try {
			user = userService.update(user);
			Notification.show("Saved", 3000, Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		} catch (InvalidException e) {
			Notification.show(e.getMessage(), 5000, Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	
	
	private void configureAvatarPanel() {
		avatarPanel.removeAll();
		currentAvatar = user.getAvatar();
		currentAvatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
		currentAvatar.getStyle().set("border", "1px solid var(--lumo-contrast-70pct)");
		avatarPanel.add(currentAvatar, imageUpload, new Label("(Max "+Config.getCurrent().getImageSizeLimitKiloBytes()+" kB, has immediate effect)"));
	}
	
	
	
}
