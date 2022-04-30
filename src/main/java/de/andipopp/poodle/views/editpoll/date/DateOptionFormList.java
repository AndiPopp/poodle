package de.andipopp.poodle.views.editpoll.date;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import de.andipopp.poodle.data.calendar.CalendarEventComparator;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.views.components.ZoneIdComboBox;
import de.andipopp.poodle.views.editpoll.AbstractOptionFormList;

/**
 * Concrete implementation of {@link AbstractOptionFormList} for {@link DateOption}s.
 * 
 * <p>It provides the time zone support needed for {@link DateOption}s via the {@link #timezone}
 * field and the {@link #timezoneSelector} input component. For data handling in the children's
 * ({@link #getOptionForms()}) {@link DateTimePicker}s, this class also provides a {@link #converter}.</p>
 * 
 * <p>This class also provides a feature to sort the {@link #getOptionForms()} with a 
 * {@link CalendarEventComparator} triggered by an entry in {@link AbstractOptionFormList#listMenuBar}. 
 * Both new interactive elements are directly added in the constructor 
 * {@link #DateOptionFormList(DatePoll, ZoneId)}.</p>
 * 
 * <p>As a concrete implementation of {@link AbstractOptionFormList}, this class does some of the
 * data handling for the {@link #getPoll()} bean by updating the connections between the 
 * {@link DateOption}s in the {@link #getOptionForms()} and the bean according to user input in
 * the method {@link #deleteAndConnect(DatePoll)}.</p>
 * 
 * @author Andi Popp
 *
 */
public class DateOptionFormList extends AbstractOptionFormList<DateOptionForm> {
	
	/**
	 * The current time zone
	 */
	private ZoneId timezone;
	
	/**
	 * A combobox to change the time {@link #timezone}
	 */
	private ZoneIdComboBox timezoneSelector;
	
	/**
	 * A converte for {@link LocalDate} to {@link Date} using the current value of {@link #timezone}
	 */
	private VariableLocalDateTimeToDateConverter converter;
	
	/**
	 * Construct a new date option form list for a given date poll and a given time zone
	 * @param poll the poll bean (cf. {@link #getPoll()})
	 * @param timezone value for {@link #timezone}
	 */
	public DateOptionFormList(DatePoll poll, ZoneId timezone) {
		super(poll);
		this.timezone = timezone;
		this.timezoneSelector = new ZoneIdComboBox(this.timezone);
		this.timezoneSelector.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		this.timezoneSelector.setMinWidth("13em");
		this.timezoneSelector.addValueChangeListener(e -> setTimezone(e.getValue()));
		
		this.converter = new VariableLocalDateTimeToDateConverter(this);
		toolbar.add(timezoneSelector);
		
		MenuItem sortOptions = listMenuBar.addItem("Sort Options");
		sortOptions.addClickListener(e -> sortList());
		
		this.setFlexGrow(1, listMenuBar);
	}

	@Override
	protected void buildList() {
		setOptionForms(new ArrayList<>());
		for(DateOption option : getPoll().sortOptions()) {
			addDateOption(option);
		}
	}
	
	/* =======================
	 * = Getters and Setters =
	 * ======================= */
	
	@Override
	public DatePoll getPoll() {
		return (DatePoll) super.getPoll();
	}

	/**
	 * Getter for {@link #timezone}
	 * @return the {@link #timezone}
	 */
	public ZoneId getTimezone() {
		return timezone;
	}
	
	/**
	 * Setter for {@link #timezone}.
	 * Ensures the {@link DateTimePicker}s of the {@link #getOptionForms()} are updated accordingly.
	 * @param timezone the {@link #timezone} to set
	 */
	public void setTimezone(ZoneId timezone) {
		if (this.timezone != null) {
			for (DateOptionForm form : getOptionForms()) {
				form.updateDateTimePickers(this.timezone, timezone);
			}
		}
		this.timezone = timezone;
	}


	/**
	 * Getter for {@link #converter}
	 * @return the {@link #converter}
	 */
	public VariableLocalDateTimeToDateConverter getConverter() {
		return converter;
	}
	
	/* ==========
	 * = Events =
	 * ========== */
	
	/**
	 * Sort the {@link #getOptionForms()} list via {@link CalendarEventComparator}
	 * an {@link #reAttachForms()}.
	 */
	private void sortList() {
		getOptionForms().sort(new CalendarEventComparator());
		reAttachForms();
	}
	
	/**
	 * Re-attach all forms in the sequence of {@link #getOptionForms()}
	 */
	private void reAttachForms() {
		for(DateOptionForm form : getOptionForms()) {
			this.add(form);
		}
	}
	
	/* =================
	 * = Data Handling =
	 * ================= */

	/**
	 * Align the options in this option forms list with the given {@link DatePoll} bean.
	 * I.e. remove all options who are already in the poll but are marked to be deleted
	 * and add all options, including those which have been newly created.
	 * @param poll the poll bean to connect the options to.
	 */
	public void deleteAndConnect(DatePoll poll) {
		for(DateOptionForm form : getOptionForms()) {
			if (form.isDelete()) {
				poll.removeOption(form.getOption());
			} else if (!poll.getOptions().contains(form.getOption())) {
				poll.addOption(form.getOption());
			}
		}
	}

	/**
	 * Add a form to the given date option to the UI and the {@link #getOptionForms()} list.
	 * @param option the option to add a form for
	 */
	public void addDateOption(DateOption option) {
		DateOptionForm form = new DateOptionForm(option, this);
		form.buildAll();
		form.loadData();
		form.addAddDateOptionEventListener(event -> {
			if (event.getOption() != null) addDateOption(event.getOption());
			else Notification.show("Enter dates before cloning").addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
		getOptionForms().add(form);
		addForm(form);
	}
	
	@Override
	public void addEmptyForm() {
		addDateOption(new DateOption());
	}
	
	
}
