package de.andipopp.poodle.views.editpoll;

import java.util.UUID;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.views.MainLayout;
import de.andipopp.poodle.views.PollView;
import de.andipopp.poodle.views.components.ConfirmationDialog;
import de.andipopp.poodle.views.components.DebugLabel;
import de.andipopp.poodle.views.components.ImageUpload;
import de.andipopp.poodle.views.components.ImageUpload.ImageReceiver;
import de.andipopp.poodle.views.components.PoodleAvatar;
import de.andipopp.poodle.views.editpoll.date.DateOptionFormList;
import de.andipopp.poodle.views.mypolls.MyPollsView;

@PageTitle("Edit Poodle Poll")
@Route(value = EditPollView.ROUTE, layout = MainLayout.class)
@PermitAll
public class EditPollView extends PollView implements ValueChangeListener<ValueChangeEvent<?>>, BeforeLeaveObserver  {

	public static final String ROUTE = "edit";
	
    public static final String CREATE_POLL_KEY = "createPoll";

    /**
     * Max width at which the two colum form layout looks appealing
     */
    private static final String MAX_WIDTH = "1000px";
    
    //three steps in editing a poll
    
    private PollCoredataForm pollCoredataForm;
    
    private PollSettingsForm pollSettingsForm;
    
    private ImageUpload imageUpload;
    
    /**
     * Temporary UUID to upload the image to before save is hit
     */
    private UUID tempImageUUID;
    
    private AbstractOptionFormList<? extends AbstractOptionForm> optionFormList;
    
    //each step gets an accordion panel
    
    private AccordionPanel pollCoredataFormPanel;
    
    private AccordionPanel pollSettingsFormPanel;
    
    private AccordionPanel imageUploadPanel;
 
    
    private AccordionPanel optionFormListPanel;
    
    //the accordion to display them
    
    private Accordion formAccordion;
    
    //buttons to navigate between the accordion panels and modify the poll
    
    private Button deletePollButton = new Button("Delete Poll");
    
    private Button toStep2Button = new Button("Next Step");
    
    private Button toStep3Button = new Button("Next Step");
    
    private Button toStep4Button = new Button("Next Step");
    
    private Button savePollButton = new Button("Save Poll");
    
    private Button gotoVoteViewButton = new Button("View Poll");
    
    //specific binder for each possible type of poll to bind the poll's direct data
    Binder<DatePoll> datePollBinder;
    
    /**
     * Remember if there are any changes in the poll to warn the user if the leave 
     */
    private boolean hasChanges = false;
    
    /**
     * Constructor to remember the services
     * @param userService the user service
     * @param pollService the poll service
     */
	public EditPollView(AuthenticatedUser authenticatedUser, PollService pollService) {
		super(authenticatedUser, pollService);
		this.add(notFound());
		
		tempImageUUID = UUID.randomUUID();
		
		//configure buttons
		toStep2Button.addClassName("primary-text");
		toStep2Button.addClickListener(e -> pollSettingsFormPanel.setOpened(true));
		toStep3Button.addClassName("primary-text");
		toStep3Button.addClickListener(e -> imageUploadPanel.setOpened(true));
		toStep4Button.addClassName("primary-text");
		toStep4Button.addClickListener(e -> optionFormListPanel.setOpened(true));
		deletePollButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		deletePollButton.addClickListener(e -> confirmDeletePoll());
		savePollButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		savePollButton.addClickListener(e -> updatePoll());
		gotoVoteViewButton.addClassName("primary-text");
		gotoVoteViewButton.addClickListener(e -> UI.getCurrent().navigate("poll", poll.buildQueryParameters(false)));
    }



	@Override
	protected void loadPoll(AbstractPoll<?, ?> poll) {	//TODO this methods is quite bulky and needs some restructuring
		super.loadPoll(poll);
		this.removeAll(); //remove the poll not found page
		
		VerticalLayout content = new VerticalLayout();
		content.setPadding(false);
		content.setMaxWidth(MAX_WIDTH);
		HorizontalLayout contentWrapper = new HorizontalLayout(content);
		contentWrapper.setJustifyContentMode(JustifyContentMode.CENTER);
		contentWrapper.setWidthFull();
		this.add(contentWrapper);
		
		//check access
		if (!userHasAccess()) {
			this.add(new Label("Access denied"));
			return;
		}
		
		//create the accordion
		formAccordion = new Accordion();
		formAccordion.setWidthFull();
		content.add(formAccordion);
		
		//build core elements and wrap them with their buttons
		pollCoredataForm = new PollCoredataForm(this.poll);
		VerticalLayout pollCoredataFormWrapper = new VerticalLayout(pollCoredataForm, toStep2Button);
		pollCoredataFormWrapper.setPadding(false);
		pollCoredataFormWrapper.setDefaultHorizontalComponentAlignment(Alignment.END);
		pollCoredataFormPanel = new AccordionPanel("Step 1: Setup Poll", pollCoredataFormWrapper);
		formAccordion.add(pollCoredataFormPanel);
		
		pollSettingsForm = new PollSettingsForm(this.poll);
		VerticalLayout pollSettingsFormWrapper = new VerticalLayout(pollSettingsForm, toStep3Button);
		pollSettingsFormWrapper.setPadding(false);
		pollSettingsFormWrapper.setDefaultHorizontalComponentAlignment(Alignment.END);
		pollSettingsFormPanel = new AccordionPanel("Step 2: Poll Settings", pollSettingsFormWrapper);
		formAccordion.add(pollSettingsFormPanel);
		
		imageUpload = new ImageUpload(new ImageReceiver(Config.getCurrent().getPollImagePath(), tempImageUUID));

			
		HorizontalLayout imageUploadWithLimit = new HorizontalLayout(imageUpload, new Label("(max "+Config.getCurrent().getImageSizeLimitKiloBytes()+" kB)" ));
		imageUploadWithLimit.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		HorizontalLayout imageUploadWrapper = new HorizontalLayout(imageUploadWithLimit, toStep4Button);
		imageUploadWrapper.setPadding(false);
		imageUploadWrapper.setDefaultVerticalComponentAlignment(Alignment.END);
		imageUploadWrapper.setJustifyContentMode(JustifyContentMode.BETWEEN);
		imageUploadPanel = new AccordionPanel("Step 3: Optional Poll Image", imageUploadWrapper);
		formAccordion.add(imageUploadPanel);
		
		
		if (poll instanceof DatePoll) 
			optionFormList = new DateOptionFormList((DatePoll) this.poll, TimeUtils.getUserTimeZone(getCurrentUser()));
		optionFormList.buildList();
		optionFormList.setPadding(false);

		optionFormListPanel = new AccordionPanel("Step 4: Define Options", optionFormList);
		formAccordion.add(optionFormListPanel);
		
		//add the final buttons
		
		HorizontalLayout deleteButtonPanel = new HorizontalLayout();
		DebugLabel debugLabel = new DebugLabel(this.poll);
		
		deleteButtonPanel.add(deletePollButton, debugLabel);
		deleteButtonPanel.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		
		HorizontalLayout buttonBar = new HorizontalLayout(
				new HorizontalLayout(deleteButtonPanel),
				new HorizontalLayout(gotoVoteViewButton, savePollButton)
		);
		
		deletePollButton.setVisible(poll != null && poll.getId() != null);
		debugLabel.setVisible(poll != null && poll.getId() != null);
		gotoVoteViewButton.setVisible(poll != null && poll.getId() != null);
		
		buttonBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
		buttonBar.setWidthFull();
		content.add(buttonBar);
		
		//load the data
		bindAndLoad(); 
		
		//reset the changes
		hasChanges = false;
		
		pollCoredataForm.addValueChangeListenerToFields(this);
		pollSettingsForm.addValueChangeListenerToFields(this);
		optionFormList.addValueChangeListenerToFields(this);
	}
	
	@Override
	protected boolean parseQueryParameters(QueryParameters queryParameters) {
		//We check if we find the parameter to build a new poll
		if (queryParameters.getParameters().containsKey(CREATE_POLL_KEY)) {
			String pollType;
			if (queryParameters.getParameters().get(CREATE_POLL_KEY).size() > 0) {
				pollType = queryParameters.getParameters().get(CREATE_POLL_KEY).get(0);
			} else {
				pollType = "";
			}
			
			AbstractPoll<?, ?> newPoll = null;
			switch (pollType) {
			case DatePoll.TYPE_NAME:
				newPoll = new DatePoll();
				break;
			default:
				showTypeNotFound(pollType);
				break;
			}
			
			if (newPoll != null) {
				newPoll.setOwner(currentUser);
				loadPoll(newPoll);
			}
			
			//we handled the parameters here
			return true;
		} else {
			//let PollView parse the query parameters
			return false;
		}
	}

	/**
	 * Checks if the user has access to edit this poll.
	 * For this, the poll itself must allow {@link AbstractPoll#canEdit(de.andipopp.poodle.data.entity.User)}
	 * and if the poll has an {@link AbstractPoll#getEditKey()} it must match this form's (read from query
	 * parameters).
	 * @return
	 */
	private boolean userHasAccess() {
		if (getPoll() == null) return false; //What are you doing here? There is no poll to edit!
		return poll.canEdit(getCurrentUser()) && (poll.getEditKey() == null || (getEditKey() != null && poll.getEditKey().equals(getEditKey())));
	}
	
	private void bindAndLoad() {
		if (this.poll instanceof DatePoll) {
			datePollBinder = new BeanValidationBinder<>(DatePoll.class);
			
			//Bind the deleteBy date first, so we can configure a validator ...
			;
			datePollBinder.forField(pollCoredataForm.deleteDate)
				.asRequired("Must specify a delete date")
				.withValidator(
					deleteDate -> Config.getCurrent().checkDeleteDate(deleteDate),
					"Delete date must be within " +Config.getCurrent().getMinPollRetentionDays() + " to " + Config.getCurrent().getMaxPollRetentionDays() + " days (" + Config.getCurrent().getEarliestPollRetentionDate() + " to " + Config.getCurrent().getLatestPollRetentionDate()+ ")"
				)
				.bind("deleteDate");
			//... bind the rest with bindInstanceFields, it will ignore the delete date (https://vaadin.com/docs/latest/flow/binding-data/components-binder-beans)
			datePollBinder.bindInstanceFields(pollCoredataForm);
			datePollBinder.bindInstanceFields(pollSettingsForm);
			datePollBinder.readBean((DatePoll) this.poll);
		}
	}
	
	private void showTypeNotFound(String type) {
		this.removeAll();
		this.add(new ErrorMessage("Unknown Poll Type","The poll type '"+type+"' can not be interpreted as a valid poll type"));
	}
	
	/* =========================
	 * = User triggered events =
	 * ========================= */
	
	/**
	 * Show a confirmation dialog for the user to confirm the poll deletion
	 */
	private void confirmDeletePoll() {
		Button ok = new Button("OK");
		ok.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		
		Button cancel = new Button("Canel");
		cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		HorizontalLayout buttons = new HorizontalLayout(cancel, ok);
		buttons.setJustifyContentMode(JustifyContentMode.BETWEEN);
		buttons.setWidthFull();
		buttons.setMinWidth("240px"); //to keep ok and cancel button far enough apart
		Label warning = new Label("This can not be undone!");
		warning.addClassName("warning-text");
		VerticalLayout dialogLayout = new VerticalLayout(new Label("Delete poll '"+poll.getTitle()+"'?"), warning, buttons);
		dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		dialogLayout.addClassName("dialog-error");
		Dialog deleteConfirmDialog = new Dialog(dialogLayout);
		deleteConfirmDialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
		deleteConfirmDialog.setModal(true);
		
		cancel.addClickListener(e -> deleteConfirmDialog.close());
		ok.addClickListener(e -> {
			if (deletePoll()) deleteConfirmDialog.close();
		});
		
		deleteConfirmDialog.open();
	}
	
	/**
	 * Actually delete the poll.
	 * Should be called from {@link #confirmDeletePoll()}
	 */
	private boolean deletePoll() {
		//double-check that the user has access
		if (!userHasAccess()) {
			Notification.show("Access denied").addThemeVariants(NotificationVariant.LUMO_ERROR);
			return false;
		} else {
			pollService.delete(getPoll());
			UI.getCurrent().navigate(MyPollsView.class);
			return true;
		}
	}

	private void updatePoll() {
		//validate the input fields
		if (!allValid()) {
			Notification.show("Invalid inputs detected. Please fix the marked issues before saving")
				.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		//write the input fields into the poll and option beans
		writeAllIfValid();
		
		//delete and connect the options to the poll
		if (!deleteAndConnectOptionsToPoll()) return;
		
		//write to back-end
		AbstractPoll<?,?> tempPoll = pollService.update(poll);
		
		//if an image has been uploaded to the temp UUID, move if to the poll's UUID. New polls will now have an UUID after update
		ImageUpload.moveTempImage(PoodleAvatar.Type.POLL, tempImageUUID, tempPoll.getId());
		
		Notification succ = Notification.show("Poll saved!");
		succ.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		succ.setPosition(Position.BOTTOM_CENTER);
		
		//load the result (which will also rest the hasChanges flag)
		loadPoll(tempPoll);
		
	}
	
	/**
	 * Check if all {@link Binder}s return true for {@link BinderValidationStatus#isOk()}.
	 * This includes the type specific binder for the poll itself as well as the binders
	 * for each option
	 * @return true if {@link BinderValidationStatus#isOk()} returns true for all {@link Binder}s, false otherwise
	 */
	private boolean allValid() {
		boolean result = true;
		
		//check the poll specific binders first
		if (poll instanceof DatePoll) result = result & datePollBinder.validate().isOk();
		
		//check the options
		for(AbstractOptionForm optionForm : optionFormList.getOptionForms()) {
			result = result & optionForm.validateNonDeleteFlagged();
		}
		
		return result;
	}

	/**
	 * Write all bound input fields if they are valid.
	 * Should be used after checking {@link #allValid()}.
	 */
	private void writeAllIfValid() {
		if (poll instanceof DatePoll) datePollBinder.writeBeanIfValid((DatePoll) poll);
		
		for(AbstractOptionForm optionForm : optionFormList.getOptionForms()) {
			optionForm.writeIfValidAndNotDeleteFlagged();
		}
	}
	
	/**
	 * Delete flagged options from poll and add new options to poll
	 */
	private boolean deleteAndConnectOptionsToPoll() {
		if (poll instanceof DatePoll && optionFormList instanceof DateOptionFormList) {
			((DateOptionFormList) optionFormList).deleteAndConnect((DatePoll) poll);
			return true;
		}else if(poll instanceof DatePoll) {
			Notification.show("Unexpected error: Poll type and list type do not match")
				.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return false;
		}
		
		Notification.show("Unexpected error: Unkown poll type")
			.addThemeVariants(NotificationVariant.LUMO_ERROR);
		return false;
	}

	@Override
	public void valueChanged(ValueChangeEvent<?> event) {
		this.hasChanges = true;
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		if (this.hasChanges) {
			ContinueNavigationAction action = event.postpone();
			ConfirmationDialog dialog = new ConfirmationDialog("Poll has unsaved changes.", "Leave anyway?");
			dialog.addOkListener(e -> action.proceed());
			dialog.open();
		}
		
	}
	
}
