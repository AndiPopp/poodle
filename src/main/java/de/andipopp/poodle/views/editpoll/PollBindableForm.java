package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;

/**
 * A form with input field which can be bound to an {@link AbstractPoll}.
 * @author Andi Popp
 *
 */
public abstract class PollBindableForm extends FormLayout {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor, shows a placeholder when form is not configured
	 */
	public PollBindableForm() {
		Span unconfigured = new Span("This form is not configured. Implement configureInputFields and remove this notice.");
		unconfigured.getElement().getThemeList().add("badge error");
		this.add(unconfigured);
	}

	/**
	 * Construct a new form and configure it according to the poll
	 * @param poll the poll used for {@link #configureInputFields(AbstractPoll)}
	 */
	public PollBindableForm(AbstractPoll<?, ?> poll) {
		this();
		configureInputFields(poll);
	}
	
	/**
	 * Configure the fields to be shown on this form.
	 * Implementations should start with this.removeAll() to remove the placeholder
	 * @param poll
	 */
	public abstract void configureInputFields(AbstractPoll<?, ?> poll);
	
}
