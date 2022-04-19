package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.views.components.DebugLabel;
import de.andipopp.poodle.views.components.HasValueFields;

/**
 * A basic layout for editing {@link AbstractOption}s.
 * @author Andi Popp
 *
 */
public abstract class AbstractOptionForm extends VerticalLayout implements HasValueFields {
	
	/**
	 * A constant to define the CSS class name for this component
	 */
	private static final String CSS_CLASS_NAME = "poll-option-form";
	
	/**
	 * A constant to define the CSS class name for components whose options are to be deleted
	 */
	private static final String DELETE_CSS_CLASS_NAME = "poll-option-form-delete"; //TODO there should be a better way to do this
	
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
	 * Footer
	 */
	protected HorizontalLayout footer = new HorizontalLayout();

	
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
	 * Label to print debug information
	 */
	private DebugLabel debugLabel = new DebugLabel();
	
	/**
	 * Label indicating the option will be deleted once 'Save Poll' is clicked
	 */
	private Label deleteOverlay = new Label("Will be deleted on 'Save Poll'");
	
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
		
		//configure the components
		deleteButton.addClickListener(e -> deleteOption());
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
		updateComponents();
		
		deleteOverlay.addClassName("poll-option-delete-overlay");
		deleteOverlay.setVisible(false);
	}

	/**
	 * Builds the complete layout by calling {@link #buildForm()} and {@link #buildFooter()}.
	 * Can be overloaded or super-called by concrete subclasses.
	 */
	public void buildAll() {
		this.removeAll();
		buildForm();
		buildFooter();
		
		//wrap the bottom bar into a layout with the debug label
		this.add(form, footer, deleteOverlay);
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
	 * Builds the {@link #footer}.
	 * Can be overloaded by concrete subclasses to add more components.
	 */
	protected void buildFooter() {
		footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
		footer.setWidthFull(); //important so that any MenuBars in the footer overflow correctly
		footer.removeAll(); 
		
		HorizontalLayout deleteButtonBarWrapper = new HorizontalLayout(deleteButton, debugLabel);
		deleteButtonBarWrapper.setPadding(false);
		deleteButtonBarWrapper.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

		footer.add(deleteButtonBarWrapper);
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
	
	/**
	 * Getter for {@link #delete}
	 * @return the {@link #delete}
	 */
	public boolean isDelete() {
		return delete;
	}
	
	/* =================
	 * = Data Handling =
	 * ================= */

	public abstract void loadData();
	
	public boolean validateNonDeleteFlagged() {
		return delete || validate();
	}
	
	public abstract boolean validate();
	
	public void writeIfValidAndNotDeleteFlagged() {
		if (!delete) writeIfValid();
	}
	
	public abstract void writeIfValid();
	
	public void configureDebugLabel() {
		debugLabel.setText(option);
	}
	
	/* ==========
	 * = Events =
	 * ========== */
	
	/**
	 * Delete the option associated with this form.
	 * If the option has already been persisted to the backed, i.e. has a non-null 
	 * {@link AbstractOption#getId()}, we toggle the {@link #delete} flag. For newly 
	 * added options we trigger an immediate {@link RemoveOptionFormEvent}.
	 */
	private void deleteOption() {
		if (option.getId() == null) {
			fireEvent(new RemoveOptionFormEvent(this));
		} else {
			delete = !delete;
			updateComponents();
		}
	}

	/**
	 * Update the components according to {@link #delete}
	 */
	protected void updateComponents() {
		deleteOverlay.setVisible(delete);
		title.setEnabled(!delete);
		if (delete) {
			deleteButton.setText("Undelete");
			deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			this.addClassName(DELETE_CSS_CLASS_NAME);
		} else {
			deleteButton.setText("Delete");
			this.removeClassName(DELETE_CSS_CLASS_NAME);
			deleteButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
		}
	}
	
	@Override
	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener) {
		HasValueFields.addValueChangeListenerToOnBlurTextField(title, listener);
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
	 * can immediately removed, typically because it contains a newly created option which was
	 * marked to be removed via the {@link AbstractOptionForm#deleteButton}.
	 * @author Andi Popp
	 *
	 */
	public static class RemoveOptionFormEvent extends ComponentEvent<AbstractOptionForm> {
		
		/**
		 * Create a new Event with the given source
		 * @param source the {@link AbstractOptionForm} to remove
		 */
		public RemoveOptionFormEvent(AbstractOptionForm source) {
			super(source, false);
		}
	}

}
