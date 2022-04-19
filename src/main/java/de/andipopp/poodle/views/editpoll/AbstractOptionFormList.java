package de.andipopp.poodle.views.editpoll;

import java.util.List;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.views.components.HasValueFields;

/**
 * Abstract version of a list of {@link AbstractOptionForm}s to edit an {@link AbstractPoll}'s options.
 * The specific poll is remembered by the field {@link #poll}.
 * 
 * <p>This is a {@link VerticalLayout} component which displays each {@link AbstractOptionForm}, but it
 * also has a list {@link #optionForms} to store its children.</p>
 * 
 * <p>This class provides a basic layout and common features for all types of {@link AbstractPoll}s. 
 * Specific features need to be defined by concrete sub-classes</p>
 * 
 * <p>Since data handling is very specific to the actual type of {@link AbstractPoll}, it is entirely
 * left to the implementing sub-classes.</p>
 * 
 * @author Andi Popp
 *
 * @param <F> The type of {@link AbstractOptionForm}
 */
public abstract class AbstractOptionFormList<F extends AbstractOptionForm> extends VerticalLayout implements HasValueFields {

	private final AbstractPoll<?, ?> poll;
	
	private List<F> optionForms;

	protected HorizontalLayout toolbar = new HorizontalLayout();
	
	protected MenuBar listMenuBar = new MenuBar();
	
	/**
	 * Construct a new form list for the given poll
	 * @param poll the poll whose options are edited by this form
	 */
	public AbstractOptionFormList(AbstractPoll<?, ?> poll) {
		this.poll = poll;
		toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
		toolbar.setWidthFull();
		toolbar.add(listMenuBar);
		this.add(toolbar);
		listMenuBar.addClassName("primary-text");
		listMenuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL);
		listMenuBar.setMinWidth("var(--lumo-size-m)");
		
		MenuItem addNewOption = listMenuBar.addItem("Add Option");
		addNewOption.addClickListener(e -> addEmptyForm());
	}
	
	/**
	 * Build the list, i.e. fill {@link #optionForms} and add contents to this layout.
	 * This must be implemented by concrete sub-classes, since the type of form will determine
	 * how the list should look.
	 */
	protected abstract void buildList();
	
	/**
	 * Getter for {@link #poll}
	 * @return the {@link #poll}
	 */
	public AbstractPoll<?, ?> getPoll() {
		return poll;
	}

	/**
	 * Getter for {@link #optionForms}
	 * @return the {@link #optionForms}
	 */
	public List<F> getOptionForms() {
		return optionForms;
	}

	/**
	 * Setter for {@link #optionForms}
	 * @param optionForms the {@link #optionForms} to set
	 */
	public void setOptionForms(List<F> optionForms) {
		this.optionForms = optionForms;
	}
	
	@Override
	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener) {
		for(AbstractOptionForm form : getOptionForms()) {
			form.addValueChangeListenerToFields(listener);
		}
	}
	
	/**
	 * Add a new option form to this list
	 * @param form the option form to add
	 */
	public void addForm(F form) {
		form.addRemoveOptionFormEventListener(e -> removeForm(form));
		optionForms.add(form);
		this.add(form);
	}
	
	/**
	 * Remove a form from this layout
	 * @param form the form to remove
	 */
	public void removeForm(F form) {
		optionForms.remove(form);
		this.remove(form);
	}
	
	/**
	 * Add a new form with a complete new option
	 */
	public abstract void addEmptyForm();
}
