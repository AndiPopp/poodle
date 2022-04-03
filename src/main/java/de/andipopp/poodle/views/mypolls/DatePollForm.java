package de.andipopp.poodle.views.mypolls;

import java.text.DecimalFormat;
import java.util.TimeZone;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.util.TimeUtils;

/**
 * A form to edit a DatePoll's meta data (not options, not votes)
 * @author Andi Popp
 *
 */
public class DatePollForm extends FormLayout {

	DatePoll poll;

	TextField title = new TextField("Title");
	
	TextField description = new TextField("Description");
	
	TextField location = new TextField("Location");
	
	ComboBox<TimeZone> timeZone = new ComboBox<TimeZone>("Time Zone");
	
	DatePicker deleteByDate = new DatePicker("Delete by");
	
	
	
	/**
	 * @param poll
	 */
	public DatePollForm() {
		addClassName("datepoll-form");
		timeZone.setItems(TimeUtils.getSupportedTimeZonesByOffset(false));
		DecimalFormat fmt = new DecimalFormat("#0");
		fmt.setPositivePrefix("+");
		timeZone.setItemLabelGenerator(z -> "(UTC"+fmt.format(z.getRawOffset())+") "+z.getID());
		add(title, description, location, timeZone, deleteByDate);
	}
	
	
	
}
