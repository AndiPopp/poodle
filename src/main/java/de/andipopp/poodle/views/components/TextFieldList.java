package de.andipopp.poodle.views.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

/**
 * A vertical layout with a variable number of TextFields.
 * <p>This class can be used as a UI element for a user to edit a list of string.
 * The core of this class is a {@link List} of {@link TextFieldListEntry} items ({@link #entries}),
 * each representing a single value of the list. New entries are added via the {@link #addEntryButton}.
 * Removing and sorting of the list is done via the UI elements of {@link TextFieldListEntry}. which
 * call the respective method of this class.</p>
 * <p>The class also implements {@link HasValue} so it can be used with {@link ValueChangeListener}s and
 * {@link Binder}s. For this it has a field {@link #listeners} and a private class 
 * {@link TextFieldListChangeEvent}. An instance of this event is fired via 
 * {@link #fireValueChangeEvent(ValueChangeEvent)} every time a change method is executed or one of 
 * the {@link #entries} fires a {@link ValueChangeEvent}. This is done by calling the respective 
 * {@link ValueChangeListener#valueChanged(ValueChangeEvent)} method for each of the {@link #listeners}.</p>
 * @author Andi Popp
 *
 */
public class TextFieldList extends VerticalLayout implements HasValue<ValueChangeEvent<List<String>>, List<String>>, ValueChangeListener<ValueChangeEvent<String>> {

	/**
	 * The actual list of entries wrapped by this class
	 */
	private List<TextFieldListEntry> entries = new ArrayList<>();
	
	/**
	 * Button to add a new empty entry
	 */
	private Button addEntryButton;
	
	/**
	 * Set of value change listeners
	 */
	private Set<ValueChangeListener<? super ValueChangeEvent<List<String>>>> listeners;
	
	/**
	 * Create a new empty text field list
	 */
	public TextFieldList() {
		HorizontalLayout buttonLayout = new HorizontalLayout(new LineAwesomeIcon("plus-circle"), new Label("add entry"));
		addEntryButton = new Button(buttonLayout);
		addEntryButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
		addEntryButton.addClickListener(e -> addEntry(new TextFieldListEntry(this)));
		listeners = new TreeSet<>();
		update();
	}
	
	/**
	 * Create a list with the given values
	 * @param values the values to create {@link #entries}
	 */
	public TextFieldList(List<String> values) {
		this();
		setValue(values);
		update();
	}

	/**
	 * Update the list with the current {@link #entries}
	 */
	public void update() {
		this.removeAll();
		for(TextFieldListEntry entry : entries) {
			this.add(entry);
		}
		this.add(addEntryButton);
	}
	
	/**
	 * Set the values to a specific list of strings
	 * @param values the values to create {@link #entries}
	 */
	public void setValue(List<String> values) {
		TextFieldListChangeEvent event = new TextFieldListChangeEvent(this, getValue());
		this.entries.clear();
		if (values != null) {
			for (String value : values) {
				entries.add(new TextFieldListEntry(this, value));
			}
		}
		event.setValue(getValue());
		fireValueChangeEvent(event);
	}
	
	/**
	 * Get the list of values from the {@link #entries}
	 * @return the values of the {@link #entries}
	 */
	public List<String> getValue() {
		List<String> values = new ArrayList<>(entries.size());
		for (TextFieldListEntry entry : entries) {
			values.add(entry.getValue());
		}
		return values;
	}
	
	/**
	 * Add an entry to this list
	 * @param entry the entry to add
	 */
	public void addEntry(TextFieldListEntry entry) {
		TextFieldListChangeEvent event = new TextFieldListChangeEvent(this, getValue());
		entries.add(entry);
		update();
		event.setValue(getValue());
		fireValueChangeEvent(event);
	}

	/**
	 * Remove an entry from the lsit
	 * @param entry the entry to remove
	 */
	public void removeEntry(TextFieldListEntry entry) {
		TextFieldListChangeEvent event = new TextFieldListChangeEvent(this, getValue());
		if (entries.remove(entry)) {
			update();
			event.setValue(getValue());
			fireValueChangeEvent(event);
		}
	}
	
	/**
	 * Move the given entry up by one slot, if it is in the list
	 * @param entry the entry to move
	 * @return true if an entry has been moved, false otherwise
	 */
	public boolean moveUp(TextFieldListEntry entry) {
		boolean result = move(entry, true);
		return result;
	}
	
	/**
	 * Move the given entry down by one slot, if it is in the list
	 * @param entry the entry to move
	 * @return true if an entry has been moved, false otherwise
	 */
	public boolean moveDown(TextFieldListEntry entry) {
		boolean result =  move(entry, false);
		return result;
	}
	
	/**
	 * Move the given entry by one slot, if it is in the list
	 * @param entry the entry to move
	 * @param up true to move up, false to move down
	 * @return true if an entry has been moved, false otherwise
	 */
	private boolean move(TextFieldListEntry entry, boolean up) {
		TextFieldListChangeEvent event = new TextFieldListChangeEvent(this, getValue());
		boolean result = false;
		
		for(int i = 0; i < entries.size(); i++) {
			if (entries.get(i).equals(entry)) {
				if (up && i > 0) {
					TextFieldListEntry aux = entries.get(i);
					entries.set(i, entries.get(i-1));
					entries.set(i-1, aux);
					update();
					result = true;
				} else if (!up && i < entries.size()-1) {
					TextFieldListEntry aux = entries.get(i);
					entries.set(i, entries.get(i+1));
					entries.set(i+1, aux);
					update();
					result =  true;
				}
				break;
			}
		}
	
		if (result) {
			event.setValue(getValue());
			fireValueChangeEvent(event);
		}
		
		return result;
	}
	
	/**
	 * Set value change mode for the {@link #entries}
	 * @param mode the value change mode for the {@link #entries}
	 */
	public void setValueChangeMode(ValueChangeMode mode) {
		for(TextFieldListEntry entry : entries) {
			entry.setValueChangeMode(mode);
		}
	}

	@Override
	public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<String>>> listener) {
		listeners.add(listener);
		return () -> listeners.remove(listener);
	}

	private void fireValueChangeEvent(ValueChangeEvent<List<String>> event) {
		for(ValueChangeListener<? super ValueChangeEvent<List<String>>> listener : listeners) {
			listener.valueChanged(event);
		}
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		addEntryButton.setEnabled(!readOnly);
		for(TextFieldListEntry entry : entries) {
			entry.setReadOnly(readOnly);
		}
	}

	@Override
	public boolean isReadOnly() {
		return !addEntryButton.isEnabled();
	}

	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		for(TextFieldListEntry entry : entries) {
			entry.setRequiredIndicatorVisible(requiredIndicatorVisible);
		}
	}

	@Override
	public boolean isRequiredIndicatorVisible() {
		if (entries.size() > 0) return entries.get(0).isRequiredIndicatorVisible();
		else return false;
	}
	
	@Override
	public void valueChanged(ValueChangeEvent<String> event) {
		List<String> oldValues = new ArrayList<>(entries.size());
		for (TextFieldListEntry entry : entries) {
			if (event.getHasValue().equals(entry.getTextField())){
				oldValues.add(event.getOldValue());
			} else {
				oldValues.add(entry.getValue());
			}
		}
		TextFieldListChangeEvent listEvent = new TextFieldListChangeEvent(this, oldValues);
		listEvent.setValue(getValue());
		fireValueChangeEvent(listEvent);
	}
	
	private static class TextFieldListChangeEvent implements ValueChangeEvent<List<String>> {

		HasValue<?, List<String>> source;
		
		List<String> oldValue;
		
		List<String> value;
		
		/**
		 * @param source
		 * @param oldValue
		 */
		public TextFieldListChangeEvent(HasValue<?, List<String>> source, List<String> oldValue) {
			super();
			this.source = source;
			this.oldValue = oldValue;
		}

		/**
		 * Getter for {@link #oldValue}
		 * @return the {@link #oldValue}
		 */
		public List<String> getOldValue() {
			return oldValue;
		}

		/**
		 * Getter for {@link #value}
		 * @return the {@link #value}
		 */
		public List<String> getValue() {
			return value;
		}

		/**
		 * Setter for {@link #value}
		 * @param value the {@link #value} to set
		 */
		public void setValue(List<String> value) {
			this.value = value;
		}

		@Override
		public HasValue<?, List<String>> getHasValue() {
			return source;
		}

		@Override
		public boolean isFromClient() {
			return false;
		}
		
	}

	
}
