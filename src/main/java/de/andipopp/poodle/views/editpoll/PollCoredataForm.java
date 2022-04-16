package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;

/**
 * A type of {@link FormLayout} to manage core data for all types of {@link AbstractPoll}s.
 * The form has input fields for all possible types of polls which are named to be bound by a {@link Binder}.
 * The input fields to be shown are configured via {@link #configureInputFields(AbstractPoll)}.
 * @author Andi Popp
 *
 */
public class PollCoredataForm extends PollBindableForm{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Input field for {@link AbstractPoll#getTitle()}
	 */
	TextField title = new TextField("Title");
	
	/**
	 * Input field for {@link AbstractPoll#getDescription()}
	 */
	TextField description = new TextField("Description");
	
	/**
	 * Input field for {@link AbstractPoll#getDeleteDate()}
	 */
	DatePicker deleteDate = new DatePicker("Delete by");
	
	/**
	 * Input field for {@link DatePoll#getLocation()}
	 */
	TextField location = new TextField("Location");

	/**
	 * Construct a new form and configure it according to the poll
	 * @param poll the poll used for {@link #configureInputFields(AbstractPoll)}
	 */
	public PollCoredataForm(AbstractPoll<?, ?> poll) {
		configureInputFields(poll);
	}

	@Override
	public void configureInputFields(AbstractPoll<?, ?> poll) {
		removeAll();
		
		//Start with title and description
		add(title, description);
		
		//If we have a date poll, add the location before the delete date
		if (poll instanceof DatePoll) add(location);
		
		//Finish with the delete date
		add(deleteDate);
	}
	
}
