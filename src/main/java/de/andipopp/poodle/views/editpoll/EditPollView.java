package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

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
@AnonymousAllowed
public class EditPollView extends PollView {

    private static final long serialVersionUID = 1L;

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
		toStep3Button.addClassName("primary-text");
		deletePollButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		savePollButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

	@Override
	protected void loadPoll(AbstractPoll<?, ?> poll) {
		super.loadPoll(poll);
		this.removeAll(); //remove the poll not found page
		
		//create the accordion
		formAccordion = new Accordion();
		formAccordion.setWidthFull();
		add(formAccordion);
		
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
		this.add(buttonBar);
		
		//load the data
		bindAndLoad();
	}
	
	private void bindAndLoad() {
		if (this.poll instanceof DatePoll) {
			datePollBinder.bindInstanceFields(pollCoredataForm);
			datePollBinder.bindInstanceFields(pollSettingsForm);
			datePollBinder.readBean((DatePoll) this.poll);
		}
	}
	
	
	
	

}
