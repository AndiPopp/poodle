package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import de.andipopp.poodle.data.entity.polls.AbstractOption;

/**
 * A basic layout for editing {@link AbstractOption}s.
 * @author Andi Popp
 *
 */
public abstract class AbstractOptionForm extends VerticalLayout {

	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A constant to define the CSS class name for this component
	 */
	private static final String CSS_CLASS_NAME = "poll-option-form";
	
	/**
	 * The option edited by this form
	 */
	private final AbstractOption<?, ?> option; 
	
	/**
	 * The list this form is a part of
	 */
	private final AbstractOptionFormList<? extends AbstractOptionForm> list;
	
	//Outer components
	
	/**
	 * Actual form Layout for the input fields
	 */
	protected FormLayout form = new FormLayout();
	
	/**
	 * Bottom bar for interactive elements
	 */
	protected HorizontalLayout buttonBar = new HorizontalLayout();
	
	//Inner components
	
	/**
	 * Input field for {@link AbstractOption#getTitle()}
	 */
	protected TextField title = new TextField("Title");

	/**
	 * Button to delete this option from the poll.
	 */
	protected Button deleteButton = new Button("Delete");
	
	/**
	 * An auxiliary flag to mark an option as to delete.
	 * Used by {@link #deleteOption()}.
	 */
	private boolean delete = false;
	
	/**
	 * Construct a new element in the given list for the given option 
	 * @param option value for {@link #option}
	 * @param list the list to add this form to
	 */
	public AbstractOptionForm(AbstractOption<?, ?> option, AbstractOptionFormList<? extends AbstractOptionForm> list) {
		//set the option
		this.option = option;
		this.list = list;
		
		//configure style
		addClassName(CSS_CLASS_NAME);
		
		//configure the delete button
		deleteButton.addClickListener(e -> deleteOption());
		updateComponents();
	}

	/**
	 * Builds the complete layout by calling {@link #buildForm()} and {@link #buildButtonBar()}.
	 * Can be overloaded or super-called by concrete subclasses.
	 */
	public void buildAll() {
		this.removeAll();
		buildForm();
		buildButtonBar();
		this.add(form, buttonBar);
	}
	
	/**
	 * Builds the {@link #form}.
	 * Can be overloaded by concrete subclasses to add more components.
	 */
	protected void buildForm() {
		form.removeAll();
		form.add(title);
	}
	
	/**
	 * Builds the {@link #buttonBar}.
	 * Can be overloaded by concrete subclasses to add more components.
	 */
	protected void buildButtonBar() {
		buttonBar.removeAll();
		buttonBar.add(deleteButton);
	}
	
	/* =======================
	 * = Getters and Setters =
	 * ======================= */
	
	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	public AbstractOption<?, ?> getOption() {
		return option;
	}
	
	/**
	 * Getter for {@link #list}
	 * @return the {@link #list}
	 */
	public AbstractOptionFormList<? extends AbstractOptionForm> getList() {
		return list;
	}
	
	
	/* =================
	 * = Data Handling =
	 * ================= */

	public abstract void loadData();
	
	/* ================
	 * = Button Event =
	 * ================ */
	
	/**
	 * Delete the option associated with this form.
	 * If the option has already been persisted to the backed, i.e. has a non-null 
	 * {@link AbstractOption#getId()}, we toggle the {@link #delete} flag. For newly 
	 * added options we trigger an immediate {@link RemoveOptionFormEvent}.
	 */
	private void deleteOption() {
		if (option.getId() == null) {
			delete = !delete;
			updateComponents();
		} else {
			fireEvent(new RemoveOptionFormEvent(this));
		}
	}

	/**
	 * Update the components according to {@link #delete}
	 */
	private void updateComponents() {
		if (delete) {
			deleteButton.setText("Undelete");
			deleteButton.removeThemeVariants(ButtonVariant.LUMO_ERROR);
			deleteButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
			title.setEnabled(false);
		} else {
			deleteButton.setText("Delete");
			deleteButton.removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
			deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
			title.setEnabled(true);
		}
	}
	
	/* ================
	 * = Custom Event =
	 * ================ */
	
	/**
	 * Short-hand to add a listener for {@link RemoveOptionFormEvent}
	 * @param listener the listener to add
	 * @return the result of {@link #addListener(Class, ComponentEventListener)}
	 */
	public Registration addRemoveOptionFormEventListener(ComponentEventListener<RemoveOptionFormEvent> listener){
		return addListener(AbstractOptionForm.RemoveOptionFormEvent.class, listener);
	}
	
	/**
	 * Add a listener for this
	 */
	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) { 
		  return getEventBus().addListener(eventType, listener);
	}

	/**
	 * This event signals to the parent component, that the source {@link AbstractOptionForm}
	 * can immeditly removed, typically because it contains a newly created option which was
	 * marked to be removed via the {@link AbstractOptionForm#deleteButton}.
	 * @author Andi Popp
	 *
	 */
	public static class RemoveOptionFormEvent extends ComponentEvent<AbstractOptionForm> {

		/**
		 * Default serial version UID
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Create a new Event with the given source
		 * @param source the {@link AbstractOptionForm} to remove
		 */
		public RemoveOptionFormEvent(AbstractOptionForm source) {
			super(source, false);
		}
	}

}
