package de.andipopp.poodle.views.mypolls;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.util.TimeUtils;

/**
 * A form to edit a DatePoll's meta data (not options, not votes)
 * @author Andi Popp
 *
 */
public class DatePollForm extends FormLayout {

	private static final long serialVersionUID = 1L;

	Binder<DatePoll> binder = new BeanValidationBinder<>(DatePoll.class);
	
	DatePoll poll;

	TextField title = new TextField("Title");
	
	TextField description = new TextField("Description");
	
	TextField location = new TextField("Location");
	
	ComboBox<ZoneId> timeZone = new ComboBox<ZoneId>("Time Zone");
	
	DatePicker deleteDate = new DatePicker("Delete by");
	
	Button save = new Button("Save");
	
	Button delete = new Button("Delete");
	
	Button cancel = new Button("Cancel");
	
	/**
	 * @param poll
	 */
	public DatePollForm() {
		addClassName("datepoll-form");
		timeZone.setItems(TimeUtils.getSupportedZoneIdsInAlphabeticalOrder());
		DecimalFormat fmt = new DecimalFormat("#0");
		fmt.setPositivePrefix("+");
		timeZone.setItemLabelGenerator(z -> z.getDisplayName(TextStyle.NARROW, Locale.US) + " (UTC" + z.getRules().getOffset(Instant.now())+")");
		deleteDate.setMax(LocalDate.now().plusDays(Config.getCurrent().getMaxPollRetentionDays()));
		
		binder.bindInstanceFields(this);
		
		add(title, description, location, timeZone, deleteDate, createButtonLayout());
		this.setWidthFull();
	}
	
	private Component createButtonLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addClickShortcut(Key.ENTER);
		cancel.addClickShortcut(Key.ESCAPE);
		
		save.addClickListener(e -> validateAndSave());
		delete.addClickListener(e -> fireEvent(new DeleteEvent(this, poll)));
		cancel.addClickListener(e -> fireEvent(new CloseEvent(this)));
		
		return new HorizontalLayout(save, delete, cancel);
	}
	
	private void validateAndSave() {
		try {
			binder.writeBean(poll);
			fireEvent(new SaveEvent(this, poll));
		} catch (ValidationException ex) {
			ex.printStackTrace();
		}
	}

	public void setPoll(DatePoll poll) {
		this.poll = poll;
		binder.readBean(poll);
//		if (poll!=null) System.out.println(poll.getDeletyByDate());
//		if (poll!=null) deleteDate.setValue(poll.getDeletyByDate());
	}
	
	// Events
	public static abstract class ContactFormEvent extends ComponentEvent<DatePollForm> {
	  private DatePoll poll;

	  protected ContactFormEvent(DatePollForm source, DatePoll poll) { 
	    super(source, false);
	    this.poll = poll;
	  }

	  public DatePoll getPoll() {
	    return poll;
	  }
	}

	public static class SaveEvent extends ContactFormEvent {
	  SaveEvent(DatePollForm source, DatePoll poll) {
	    super(source, poll);
	  }
	}

	public static class DeleteEvent extends ContactFormEvent {
	  DeleteEvent(DatePollForm source, DatePoll poll) {
	    super(source, poll);
	  }

	}

	public static class CloseEvent extends ContactFormEvent {
	  CloseEvent(DatePollForm source) {
	    super(source, null);
	  }
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
	    ComponentEventListener<T> listener) { 
	  return getEventBus().addListener(eventType, listener);
	}
	
	
}
