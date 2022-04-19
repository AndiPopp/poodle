package de.andipopp.poodle.views.editpoll.date;

import java.time.ZoneId;
import java.util.ArrayList;

import com.vaadin.flow.component.ComponentEventListener;

import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.editpoll.AbstractOptionFormList;
import de.andipopp.poodle.views.editpoll.date.DateOptionForm.AddDateOptionEvent;

public class DateOptionFormList extends AbstractOptionFormList<DateOptionForm> implements ComponentEventListener<AddDateOptionEvent> {
	
	private ZoneId timezone;
	
	private VariableLocalDateTimeToDateConverter converter;
	
	public DateOptionFormList(DatePoll poll, ZoneId timezone) {
		super(poll);
		this.timezone = timezone;
		this.converter = new VariableLocalDateTimeToDateConverter(this);
	}

	public DateOptionFormList(DatePoll poll) {
		this(poll, VaadinUtils.guessTimeZoneFromVaadinRequest()); //TODO load from user settings
	}

	@Override
	protected void buildList() {
		setOptionForms(new ArrayList<>());
		for(DateOption option : getPoll().getOptions()) {
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
	 * Setter for {@link #timezone}
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
	
	/* =================
	 * = Data Handling =
	 * ================= */


	public void deleteAndConnect(DatePoll poll) {
		for(DateOptionForm form : getOptionForms()) {
			if (form.isDelete()) {
				poll.removeOption(form.getOption());
			} else if (!poll.getOptions().contains(form.getOption())) {
				poll.addOption(form.getOption());
			}
		}
	}

	public void addDateOption(DateOption option) {
		DateOptionForm form = new DateOptionForm(option, this);
		form.buildAll();
		form.loadData();
		form.addAddDateOptionEventListener(this);
		getOptionForms().add(form);
		addForm(form);
	}
	
	@Override
	public void addEmptyForm() {
		addDateOption(new DateOption());
	}

	/* ==========
	 * = Events =
	 * ========== */
	
	@Override
	public void onComponentEvent(AddDateOptionEvent event) {
		addDateOption(event.getOption());
	}
}
