package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

/**
 * An entry of a {@link TextFieldList}.
 * <p>This class wraps a {@link TextField} field {@link #textField} for the user to edit this specific 
 * entry. In addition to editing the text field, the user can delete the entry from or move the entry
 * within the {@link #parentList}, via the respective buttons.</p>
 * <p>The class also implements {@link HasValue}, to pass on the respetive methods of the wrapped 
 * {@link TextField} to the {@link #parentList}.</p>
 * @author Andi Popp
 *
 */
public class TextFieldListEntry extends HorizontalLayout implements HasValue<ValueChangeEvent<String>, String> {

	private final TextFieldList parentList;
	
	private final TextField textField = new TextField();
	
	private final Button remove = new Button(new LineAwesomeIcon("minus-circle"));
	
	private final Button up = new Button(new LineAwesomeIcon("caret-up"));
	
	private final Button down = new Button(new LineAwesomeIcon("caret-down"));
	
	/**
	 * Create a new entry for the given list
	 * @param parentList value for {@link #parentList}
	 */
	protected TextFieldListEntry(TextFieldList parentList) {
		this.parentList = parentList;
		remove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
		remove.addClickListener(e -> this.parentList.removeEntry(this));
		up.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		up.addClickListener(e -> this.parentList.moveUp(this));
		down.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		down.addClickListener(e -> this.parentList.moveDown(this));
		this.add(remove, textField, up, down);
		this.addValueChangeListener(this.parentList);
	}
	
	/**
	 * Create a new entry for the given list with the predefined value
	 * @param parent value for {@link #parentList}
	 * @param value preset value {@link #textField}
	 */
	protected TextFieldListEntry(TextFieldList parent, String value) {
		this(parent);
		textField.setValue(value);
	}
	
	/**
	 * Return value of {@link #textField}
	 * @return the value of {@link #textField}
	 */
	public String getValue() {
		return textField.getValue();
	}
	
	@Override
	public void setValue(String value) {
		textField.setValue(value);	
	}
	
	/**
	 * Getter for {@link #textField}
	 * @return the {@link #textField}
	 */
	protected TextField getTextField() {
		return textField;
	}

	/**
	 * Set the value change mode for this entry
	 * @param mode the value change mode for {@link #textField}
	 */
	public void setValueChangeMode(ValueChangeMode mode) {
		textField.setValueChangeMode(mode);
	}

	@Override
	public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<String>> listener) {
		return textField.addValueChangeListener(listener);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		textField.setReadOnly(readOnly);
		remove.setEnabled(!readOnly);
		up.setEnabled(!readOnly);
		down.setEnabled(!readOnly);
	}

	@Override
	public boolean isReadOnly() {
		return textField.isReadOnly();
	}

	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		textField.setRequiredIndicatorVisible(requiredIndicatorVisible);
	}

	@Override
	public boolean isRequiredIndicatorVisible() {
		return textField.isRequiredIndicatorVisible();
	}
	
	
}
