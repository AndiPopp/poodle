package de.andipopp.poodle.views.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;

import de.andipopp.poodle.util.TimeUtils;

public class ZoneIdComboBox extends ComboBox<ZoneId>{

	/**
	 * 
	 */
	public ZoneIdComboBox() {
		setItems(TimeUtils.getSupportedZoneIdsInAlphabeticalOrder());
		setLabel("Time Zone");
		setItemLabelGenerator(z -> z.getDisplayName(TextStyle.NARROW, Locale.US) + " " + z.getRules().getOffset(Instant.now()));
		setMaxWidth("50%");
	}

	public ZoneIdComboBox(ZoneId zoneId) {
		this();
		setValue(zoneId);
	}
	
	
}
