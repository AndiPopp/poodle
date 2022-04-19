package de.andipopp.poodle.views.components;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;

import de.andipopp.poodle.util.TimeUtils;

/**
 * A {@link ComboBox} offering all currently available {@link ZoneId}s in alphabetical order.
 * 
 * <p>In addition to the predefined values, this combo box will also have some typical presets, i.e.
 * a default {@link ItemLabelGenerator}, a max width of 50% and a label saying "Time Zone".</p>
 * 
 * <p>It also comes with a value change listener which will store each selected value in a cookie via
 * {@link TimeUtils#rememberZoneIdInCookie(ZoneId)}, so it can be retrieved via 
 * {@link TimeUtils#getUserTimeZone(de.andipopp.poodle.data.entity.User)}. Not that the latter will
 * always prefer specific user settings over the value stored in the cookie.</p>
 * @author Andi Popp
 *
 */
public class ZoneIdComboBox extends ComboBox<ZoneId>{

	/**
	 * Create a new zone id combo box
	 */
	public ZoneIdComboBox() {
		setItems(TimeUtils.getSupportedZoneIdsInAlphabeticalOrder());
		setLabel("Time Zone");
		setItemLabelGenerator(z -> z.getDisplayName(TextStyle.NARROW, Locale.US) + " " + z.getRules().getOffset(Instant.now()));
		setMaxWidth("50%");
		this.addValueChangeListener(e -> TimeUtils.rememberZoneIdInCookie(e.getValue()));
	}

	/**
	 * Create a new zone id combo box and set the value to the specified zone id
	 * @param zoneId the value to set the combo box to
	 */
	public ZoneIdComboBox(ZoneId zoneId) {
		this();
		setValue(zoneId);
	}
}
