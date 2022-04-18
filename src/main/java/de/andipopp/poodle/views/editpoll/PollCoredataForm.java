package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.views.HasValueFields;

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
		
		if (poll instanceof DatePoll) configureForDatePoll((DatePoll) poll);
		

	}
	
	/**
	 * Configure the form for a {@link DatePoll}
	 * @param poll the poll to configure
	 */
	private void configureForDatePoll(DatePoll poll) {
		setResponsiveSteps(
			new ResponsiveStep("0", 1),
			new ResponsiveStep("640px", 4)
		);
		add(title, location, description, deleteDate);
		setColspan(title, 2);
		setColspan(location, 2);
		setColspan(description, 3);
		setColspan(deleteDate, 1);
	}
	
	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener) {
		HasValueFields.addValueChangeListenerToOnBlurTextField(title, listener);
		HasValueFields.addValueChangeListenerToOnBlurTextField(location, listener);
		HasValueFields.addValueChangeListenerToOnBlurTextField(description, listener);
		deleteDate.addValueChangeListener(listener);
	}
	

	
}
