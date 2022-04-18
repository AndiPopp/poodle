package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public interface HasValueFields {

	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener);

	public static void addValueChangeListenerToOnBlurTextField(TextField field, ValueChangeListener<ValueChangeEvent<?>> listener) {
		field.setValueChangeMode(ValueChangeMode.ON_BLUR);
		field.addValueChangeListener(listener);
	}
}