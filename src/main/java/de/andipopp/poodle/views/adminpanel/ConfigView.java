package de.andipopp.poodle.views.adminpanel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.service.ConfigService;

public class ConfigView extends VerticalLayout{

	/**
	 * Maximum width a which this layout looks good
	 */
	private static final String MAX_WIDTH = "1000px";
	
	/**
	 * Input field for {@link Config#getWelcomeTitle()}
	 */
	private TextField welcomeTitle = new TextField("Welcome Title");
	

	/**
	 * Input field for {@link Config#getWelcomeMessage()}
	 */
	private TextField welcomeMessage = new TextField("Welcome Message");
	
	/**
	 * Input field for {@link Config#getMaxPollRetentionDays()}
	 */
	private IntegerField minPollRetentionDays = new IntegerField("Min Poll Retention (days)");

	/**
	 * Input field for {@link Config#getMaxPollRetentionDays()}
	 */
	private IntegerField maxPollRetentionDays = new IntegerField("Max Poll Retention (days)");
	
	/**
	 * Input field for {@link Config#getDefaultPollRententionDays()}
	 */
	private IntegerField defaultPollRententionDays = new IntegerField("Default Poll Retention (days)");

	/**
	 * Input field for {@link Config#isAllowOrphanPolls()}
	 */
	private Checkbox allowOrphanPolls = new Checkbox("Allow Orphan Polls");
	
	/**
	 * Input field for {@link Config#getImageSizeLimitKiloBytes()}
	 */
	private IntegerField imageSizeLimitKiloBytes = new IntegerField("Max Images Size (kB)");

	/**
	 * Input field for {@link Config#getPollImagePath()}
	 */
	private TextField pollImagePath = new TextField("Poll Image Folder");
	
	private Checkbox movePollImageContent = new Checkbox("Move poll images to new path");
	
	/**
	 * Input field for {@link Config#getUserImagePath()}
	 */
	private TextField userImagePath = new TextField("User Image Folder");

	private Checkbox moveUserImageContent = new Checkbox("Move user images to new path");
	
	private Dialog moveContentDialog;
	
	/**
	 * Binder for instance fields
	 */
	Binder<Config> binder = new BeanValidationBinder<>(Config.class);

	/**
	 * Config service to store changes;
	 */
	ConfigService configService;
	
	/**
	 * This view's config bean
	 */
	Config config;
	
	public ConfigView(ConfigService configService ) {
		this.config = Config.getCurrent();
		this.setMaxWidth(MAX_WIDTH);
		this.setHeightFull();
		
		this.configService = configService;
		binder.bindInstanceFields(this);
		binder.readBean(config);
		this.add(configureForms());
		Button save = new Button("Save Config");
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickListener(e -> prepareSaveConfig());
		HorizontalLayout buttonWrapper = new HorizontalLayout(save);
		buttonWrapper.setWidthFull();
		buttonWrapper.setJustifyContentMode(JustifyContentMode.END);
		this.add(buttonWrapper);
		
		//configure elements
		minPollRetentionDays.setHasControls(true);
		maxPollRetentionDays.setHasControls(true);
		defaultPollRententionDays.setHasControls(true);
		imageSizeLimitKiloBytes.setHasControls(true);
		configureMoveContentDialog();
	}



	private Component configureForms() {
		FormLayout welcome = new FormLayout(welcomeTitle, welcomeMessage);
		welcome.setResponsiveSteps(
			new ResponsiveStep("0", 1),
			new ResponsiveStep("640px", 3)
		);
		welcome.setColspan(welcomeTitle, 1);
		welcome.setColspan(welcomeMessage, 2);
		
		FormLayout pollRetention = new FormLayout(defaultPollRententionDays, minPollRetentionDays, maxPollRetentionDays);
		pollRetention.setResponsiveSteps(
			new ResponsiveStep("0", 3)
		);
		
		FormLayout images = new FormLayout(userImagePath, pollImagePath, imageSizeLimitKiloBytes);
		images.setResponsiveSteps(
			new ResponsiveStep("0", 1),
			new ResponsiveStep("640px", 5)
		);
		images.setColspan(userImagePath, 2);
		images.setColspan(pollImagePath, 2);
		images.setColspan(imageSizeLimitKiloBytes, 1);
		
		FormLayout other = new FormLayout(allowOrphanPolls);
		
		VerticalLayout forms = new VerticalLayout(
			new H4("Welcom Screen Configuration"),
			welcome, 
			new H4("Poll Retention Configuration"),
			pollRetention,
			new H4("Avatar Image Configuration"),
			images,
			new H4("Other Settings"),
			other
		);
		forms.setPadding(false);
		forms.setSpacing(false);
		return forms;
	}
	

	private void prepareSaveConfig() {
		if (!binder.validate().isOk()) {
			Notification.show("Please fix the invalid entries").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		updateMoveCheckboxes();
		if (movePollImageContent.getValue() || moveUserImageContent.getValue()) {
			moveContentDialog.open();
		} else {
			saveConfig();
		}
	}
	
	private void saveConfig() {
		binder.writeBeanIfValid(config);
		config = configService.save(config);
		Config.setCurrent(config);
		Notification.show("Config saved", 3000, Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	}

	private void configureMoveContentDialog() {
		Button ok = new Button("OK");
		ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		ok.addClickListener(e -> {
			try {
				prepareImagePaths();
				moveContentDialog.close();
				saveConfig();
			} catch (IOException e1) {
				Notification.show(e1.getMessage(), 7000, Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);;
			}
		});
		Button cancel = new Button("Cancel");
		cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancel.addClickListener(e -> moveContentDialog.close());
		
		HorizontalLayout buttons = new HorizontalLayout(cancel, ok);
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.BETWEEN);
		
		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setPadding(false);
		dialogLayout.add(moveUserImageContent, movePollImageContent, buttons);
		
		moveContentDialog = new Dialog(dialogLayout);
	}
	
	private void updateMoveCheckboxes() {
		movePollImageContent.setValue(!config.getPollImagePath().equals(pollImagePath.getValue()));
		movePollImageContent.setEnabled(movePollImageContent.getValue());
		moveUserImageContent.setValue(!config.getUserImagePath().equals(userImagePath.getValue()));
		moveUserImageContent.setEnabled(moveUserImageContent.getValue());
	}
	
	private void prepareImagePaths() throws IOException {
		boolean movePollContent = movePollImageContent.getValue();
		boolean moveUserContent = moveUserImageContent.getValue();
		movePollContent = movePollContent & createNewPath(config.getPollImagePath(), pollImagePath.getValue());
		moveUserContent = moveUserContent & createNewPath(config.getUserImagePath(), userImagePath.getValue());
		if (movePollContent) moveContent(config.getPollImagePath(), pollImagePath.getValue());
		if (moveUserContent) moveContent(config.getUserImagePath(), userImagePath.getValue());
	}
	
	private boolean createNewPath(String oldPath, String newPath) throws IOException {
		if (newPath.equals(oldPath)) {
			return false;
		} else {
			File newFile = new File(newPath);
			if(newFile.exists() && !newFile.isDirectory()) throw new IOException(newPath + " already exists but is no directory.");
			if(!newFile.exists()) newFile.mkdirs();
			return true;
		}
	}
	
	private void moveContent(String oldPath, String newPath) throws IOException {
		File oldFile = new File(oldPath);
		for(File file : oldFile.listFiles()) {
			try {
				if (!file.getName().startsWith(".")) FileUtils.moveFile(file, new File(newPath + System.getProperty("file.separator")+file.getName()));
			} catch (IOException e) {
				throw new IOException("Error while moving files. Folder movement is incomplete. CHECK CONTENTS!", e);
			}
		}
	}
}
