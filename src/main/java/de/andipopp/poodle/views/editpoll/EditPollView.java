package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Label;
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
    
    //specific binder for each possible type of poll
    Binder<DatePoll> datePollBinder = new BeanValidationBinder<>(DatePoll.class);
    
    
	public EditPollView(UserService userServer, PollService pollService) {
		super(userServer, pollService);
		this.add(notFound());
    }

	@Override
	protected void loadPoll(AbstractPoll<?, ?> poll) {
		super.loadPoll(poll);
		this.removeAll(); //remove the poll not found page
		
		//create the accordion
		formAccordion = new Accordion();
		formAccordion.setWidthFull();
		add(formAccordion);
		
		//build core elements
		pollCoredataForm = new PollCoredataForm(this.poll);
		pollCoredataFormPanel = new AccordionPanel("Step 1: Setup Poll", pollCoredataForm);
		formAccordion.add(pollCoredataFormPanel);
		
		pollSettingsForm = new PollSettingsForm(this.poll);
		pollSettingsFormPanel = new AccordionPanel("Step 2: Poll Settings", pollSettingsForm);
		formAccordion.add(pollSettingsFormPanel);
		
		if (poll instanceof DatePoll) 
			optionFormList = new DateOptionFormList((DatePoll) this.poll, VaadinUtils.guessTimeZoneFromVaadinRequest()); //TODO get timezone from user settings
		optionFormList.buildList();
		optionFormList.setPadding(false);
		optionFormListPanel = new AccordionPanel("Step 3: Define Options", optionFormList);
		formAccordion.add(optionFormListPanel);
		
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
