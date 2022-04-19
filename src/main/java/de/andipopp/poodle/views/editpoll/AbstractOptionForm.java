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
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.shared.Registration;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.views.components.DebugLabel;
import de.andipopp.poodle.views.components.HasValueFields;

/**
 * A basic layout for editing {@link AbstractOption}s.
 * 
 * <p>This is a sub-form to edit one specific option and is meant to be used together with
 * an {@link AbstractOptionFormList} as its parent. This parent is stored in {@link #list}.</p>
 * 
 * <p>The class provides a basic layout consisting of an input {@link FormLayout} (c.f. {@link #form})
 * and a {@link #footer} {@link HorizontalLayout} for other interactive elements like {@link Button}s.</p>
 * 
 * <p>It also provides common features for all types of {@link AbstractOption}s. Specific features 
 * need to be implemented by the concrete child classes. 
 * 
 * <p>The common features includes handling the deletion of forms and options via the {@link #deleteButton}
 * and its associated {@link #deleteOption()} event. Options which have an ID and are therefore most 
 * likely already persisted are marked for deletion via {@link #delete}. The layout will respond to this 
 * by changing the function and appearance of its components, including showing the {@link #deleteOverlay}.
 * Forms for newly added options (which do not have an ID) can be removed right away, by triggering a 
 * {@link RemoveOptionFormEvent}. </p>
 * 
 * <p>The class also implements {@link HasValueFields}, for a quick an convenient way to expose its
 * fields to a {@link ValueChangeListener} (typically an {@link EditPollView}, which will remember
 * if changes happened and warn the user if they leave with unsaved changes).</p>
 * 
 * <p>Finally the class provides the abstract structure for data handling, i.e loading data from the 
 * {@link #option}, validating values of the input fields and writing valid data to the {@link #option} 
 * bean. The latter two steps are split into {@link #validate()} and {@link #writeIfValid()} so that 
 * all the parent {@link #list}'s forms can be validated before writing any data to the #{@link #option}
 * bean. For convenience, this class provides {@link #validateNonDeleteFlagged()} and
 * {@link #writeIfValidAndNotDeleteFlagged()} to skip the validation and writing step if the 
 * {@link #option} is marked for deletion via {@link #delete} anyway. </p>
 * 
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
	
	/**
	 * Configure the {@link #debugLabel} from the {@link #option} bean
	 */
	public void configureDebugLabel() {
		debugLabel.setText(option);
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

	/**
	 * load data from the {@link #option} bean into the input fields
	 */
	public abstract void loadData();
	
	/**
	 * Validate all input fields.
	 * This has to be implemented by concrete sub-classes who know all the relevant input
	 * fields and how they are bound to the bean.
	 * 
	 * @return <code>true</code> if the validation was successful (e.g. indicated by 
	 * {@link BinderValidationStatus#isOk()}), <code>false</code> otherwise
	 */
	public abstract boolean validate();
	
	/**
	 * Convenience method to skip the {@link #validate()} step, if the options is marked for
	 * deletion via {@link #delete} anyway.
	 * @return <code>true</code> if {@link #delete} is true, otherwise the result of a call to {@link #validate()}
	 */
	public boolean validateNonDeleteFlagged() {
		return delete || validate();
	}
	
	/**
	 * Write valid data from the input fields to the {@link #option} bean.
	 * This should typically be used after {@link #validate()} was successful, i.e. returned <code>true</code>.
	 */
	public abstract void writeIfValid();
	
	/**
	 * Convenience method to only call {@link #writeIfValid()} if the option is not marked for deletion via {@link #delete}.
	 */
	public void writeIfValidAndNotDeleteFlagged() {
		if (!delete) writeIfValid();
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
	 * Update the components according to {@link #delete} to ensure clarity for the user.
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
	 * Add a listener for {@link RemoveOptionFormEvent}
	 * @param listener the listener to add
	 * @return the result of {@link #addListener(Class, ComponentEventListener)}
	 */
	public Registration addRemoveOptionFormEventListener(ComponentEventListener<RemoveOptionFormEvent> listener){
		return addListener(AbstractOptionForm.RemoveOptionFormEvent.class, listener);
	}
	
	/**
	 * This event signals an {@link AbstractOptionForm}'s parent {@link AbstractOptionForm#list}, that 
	 * the source {@link AbstractOptionForm} can immediately removed, typically because it contains a 
	 * newly created option which was triggered to be removed via the {@link AbstractOptionForm#deleteButton}.
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
