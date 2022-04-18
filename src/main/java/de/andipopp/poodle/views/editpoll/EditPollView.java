package de.andipopp.poodle.views.editpoll;

import java.time.LocalDate;
import java.time.Period;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.MainLayout;
import de.andipopp.poodle.views.PollView;
import de.andipopp.poodle.views.editpoll.date.DateOptionFormList;

@PageTitle("Edit Poodle Poll")
@Route(value = "edit", layout = MainLayout.class)
@PermitAll
public class EditPollView extends PollView {

    private static final long serialVersionUID = 1L;

    /**
     * Max width at which the two colum form layout looks appealing
     */
    private static final String MAX_WIDTH = "1000px";
    
    //three steps in editing a poll
    
    private PollCoredataForm pollCoredataForm;
    
    private PollSettingsForm pollSettingsForm;
    
    private AbstractOptionFormList<? extends AbstractOptionForm> optionFormList;
    
    //each step gets an accordion panel
    
    private AccordionPanel pollCoredataFormPanel;
    
    private AccordionPanel pollSettingsFormPanel;
    
    private AccordionPanel optionFormListPanel;
    
    //the accordion to display them
    
    private Accordion formAccordion;
    
    //buttons to navigate between the accordion panels and modify the poll
    
    private Button deletePollButton = new Button("Delete Poll");
    
    private Button toStep2Button = new Button("Next Step");
    
    private Button toStep3Button = new Button("Next Step");
    
    private Button savePollButton = new Button("Save Poll");
    
    //specific binder for each possible type of poll to bind the poll's direct data
    Binder<DatePoll> datePollBinder = new BeanValidationBinder<>(DatePoll.class);
    
    /**
     * Constructor to remember the services
     * @param userService the user service
     * @param pollService the poll serivice
     */
	public EditPollView(UserService userService, PollService pollService) {
		super(userService, pollService);
		this.add(notFound());
		
		//configure buttons
		toStep2Button.addClassName("primary-text");
		toStep2Button.addClickListener(e -> pollSettingsFormPanel.setOpened(true));
		toStep3Button.addClassName("primary-text");
		toStep3Button.addClickListener(e -> optionFormListPanel.setOpened(true));
		deletePollButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		savePollButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

	@Override
	protected void loadPoll(AbstractPoll<?, ?> poll) {	
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
		
		if (poll instanceof DatePoll) 
			optionFormList = new DateOptionFormList((DatePoll) this.poll, VaadinUtils.guessTimeZoneFromVaadinRequest()); //TODO get timezone from user settings
		optionFormList.buildList();
		optionFormList.setPadding(false);
		optionFormListPanel = new AccordionPanel("Step 3: Define Options", optionFormList);
		formAccordion.add(optionFormListPanel);
		
		//add the final buttons
		
		HorizontalLayout deleteButtonPanel = new HorizontalLayout();
		if (poll.getId() != null) { //we have a poll which is stored, so it can be deleted
			deleteButtonPanel.add(deletePollButton);
			deleteButtonPanel.add(new DebugLabel(this.poll));
		}
		deleteButtonPanel.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		HorizontalLayout buttonBar = new HorizontalLayout(
				deleteButtonPanel,
				savePollButton
		);
		buttonBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
		buttonBar.setWidthFull();
		content.add(buttonBar);
		
		//load the data
		bindAndLoad();
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
			//Bind the deleteBy date first, so we can configure a validator ...
			;
			datePollBinder.forField(pollCoredataForm.deleteDate)
				.asRequired("Must specify a delete date")
				.withValidator(
					deleteDate -> Config.getCurrent().checkDeleteDate(deleteDate),
					"Delete date must be within " + Config.getCurrent().getMaxPollRetentionDays() + " days (latest " + Config.getCurrent().getLatestPollRetentionDate()+ ")"
				)
				.bind("deleteDate");
			//... bind the rest with bindInstanceFields, it will ignore the delete date (https://vaadin.com/docs/latest/flow/binding-data/components-binder-beans)
			datePollBinder.bindInstanceFields(pollCoredataForm);
			datePollBinder.bindInstanceFields(pollSettingsForm);
			datePollBinder.readBean((DatePoll) this.poll);
		}
	}
	
	
	
	

}
