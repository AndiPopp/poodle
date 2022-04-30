package de.andipopp.poodle.views.editpoll.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import de.andipopp.poodle.data.calendar.CalendarEvent;
import de.andipopp.poodle.data.calendar.CalendarEventComparator;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.views.components.HasValueFields;
import de.andipopp.poodle.views.editpoll.AbstractOptionForm;

/**
 * Layout to edit {@link DateOption}s.
 * It adds the necessary input fields for {@link DateOption} specific fields, time zone support and 
 * a few convenience features for adding new options. It also implements the data handling specified
 * by {@link AbstractOptionForm}. It's parent must be a {@link DateOptionFormList} and its bean
 * a {@link DateOption}. The constructor {@link #DateOptionForm(DateOption, DateOptionFormList)}
 * only accepts these argument types and {@link #getList()} and {@link #getOption()} will class 
 * cast to {@link DateOptionFormList} accordingly.
 * 
 * <p>Extra fields (i.e in fields in addition to {@link #getTitle()}) include {@link #location},
 * {@link #startPicker} and {@link #endPicker}. While the former two fields use the {@link #binder}'s
 * default data handling features, the latter two provide {@link LocalDate} values while the bean
 * expects {@link Date} values. For this, the form will use its parent's 
 * {@link DateOptionFormList#getTimezone()} and associated {@link DateOptionFormList#getConverter()}.</p>
 * 
 * <p>A further validation of the {@link #binder} compares the value of the {@link #endPicker} to the
 * one of the {@link #startPicker}, ensuring the former is before the latter via {@link #validateEndDate(Date)}.</p>
 * 
 * <p>{@link DatePoll} specific features are provided via the {@link #rightFooterMenu}. They include
 * the possibility to show and hide the optional input fields {@link #getTitle()} and
 * {@link #location}, as well as an option to clone the {@link #getOption()} into a new {@link DateOptionForm}.
 * The latter is achieved by creating a new {@link DateOption} from the current input field values via
 * and an optional time offset in days via {@link #createFromFields(int)} and than sending it via an 
 * {@link AddDateOptionEvent} to a listener in the parent {@link DateOptionFormList}.</p>
 * 
 * <p>Time zone support is handled by the parent {@link #getList()}, but for convenience this class
 * also provides a method {@link #updateDateTimePickers(ZoneId, ZoneId)} to update the {@link #startPicker}
 * and {@link #endPicker} accordingly so the actual {@link Instant}/{@link Date} they represent stays 
 * the same.</p>
 * 
 * <p>This class also implements {@link CalendarEvent}, so that a list of this type of form can be
 * sorted via a {@link CalendarEventComparator}.</p>
 * 
 * @author Andi Popp
 *
 */
public class DateOptionForm extends AbstractOptionForm implements CalendarEvent{
	
	//DateOption specific components
	
	/**
	 * Binder to for input fields data handling
	 */
	private Binder<DateOption> binder = new BeanValidationBinder<>(DateOption.class);
	
	/**
	 * Input field for {@link DateOption#getLocation()}
	 */
	TextField location = new TextField("Location");
	
	/**
	 * Input field for {@link DateOption#getStart()}.
	 * This one cannot be managed by a binder because of time zone support.
	 */
	DateTimePicker startPicker = new DateTimePicker("Start Date");
	
	/**
	 * Input field for {@link DateOption#getEnd()}.
	 * This one cannot be managed by a binder because of time zone support.
	 */
	DateTimePicker endPicker = new DateTimePicker("End Date");
	
	
	/**
	 * Menu bar offering more features on the right footer
	 */
	MenuBar rightFooterMenu = new MenuBar();
	
	/* =============================
	 * = Constructors and Builders =
	 * ============================= */
	
	/**
	 * Construct a new form with the given arguments.
	 * The constructor configures the input fields and prepares the {@link #binder}.
	 * @param option the value for {@link #getOption()} bean
	 * @param timeZone the parent {@link DateOptionFormList}
	 */
	public DateOptionForm(DateOption option, DateOptionFormList list) {
		//call the super-constructor to set the bean and parent list form
		super(option, list);
		
		//configure the specific UI components
		endPicker.setRequiredIndicatorVisible(true);
		startPicker.setRequiredIndicatorVisible(true);
		location.setRequired(false);
		location.setVisible(false);
		title.setRequired(false);
		title.setVisible(false);
		configureRightFooterMenu();
		
		//prepare the binder
		binder.forField(startPicker)
			.withConverter(getList().getConverter())
			.asRequired()
			.bind(DateOption::getStart, DateOption::setStart);
		binder.forField(endPicker)
			.withConverter(getList().getConverter())
			.asRequired()
			.withValidator(endDate -> validateEndDate(endDate), "End date must be after start date")
			.bind(DateOption::getEnd, DateOption::setEnd);
		//bind the rest of the fields
		binder.bindInstanceFields(this);
	}
	
	/**
	 * Build a menu bar for the right side of the {@link AbstractOptionForm#footer}.
	 * The menu bar adds features to show/hide the optional {@link #getTitle()} and 
	 * {@link #getLocation()} input fields, as well as cloning the event.
	 */
	private void configureRightFooterMenu() {
		rightFooterMenu.setMinWidth("var(--lumo-size-m)"); // <-- ! needed to change min-width from `auto` so it could shrink !
		rightFooterMenu.addThemeVariants(MenuBarVariant.LUMO_SMALL);
		MenuItem showMore = rightFooterMenu.addItem("Show More");
		showMore.addClickListener(e -> {
			location.setVisible(!location.isVisible());
			title.setVisible(location.isVisible());
			if (location.isVisible()) showMore.setText("Show Less");
			else showMore.setText("Show More");
		});
		MenuItem clone = rightFooterMenu.addItem("Clone");
		clone.addClickListener(e -> fireEvent(new AddDateOptionEvent(this, createFromFields(0))));
		MenuItem clonePlus1D = rightFooterMenu.addItem("Clone+1day");
		clonePlus1D.addClickListener(e -> fireEvent(new AddDateOptionEvent(this, createFromFields(1))));
		MenuItem clonePlus1W = rightFooterMenu.addItem("Clone+1week");
		clonePlus1W.addClickListener(e -> fireEvent(new AddDateOptionEvent(this, createFromFields(7))));
	}
	
	/**
	 * Check if the argument is after the {@link #startPicker}'s.
	 * @param endDate the date to be compared to {@link #startPicker}, typically the value of {@link #endPicker}
	 * @return true if the argument is after the date of {@link #endPicker} (at the time zone of {@link #getList()})
	 */
	private boolean validateEndDate(Date endDate) {
		Date start = Date.from(startPicker.getValue().atZone(getList().getTimezone()).toInstant());
		return start.before(endDate);
	}

	@Override
	protected void buildForm() {
		form.removeAll();
		form.add(startPicker, endPicker, title, location);
	}
	
	@Override
	protected void buildFooter() {
		super.buildFooter();
		footer.add(rightFooterMenu);
//		footer.setFlexGrow(1, rightFooterMenu); // <- this allows the menu to regrow, but it also makes it use up more space than it needs and screws with the BETWEEN justification
	}

	
	/* =======================
	 * = Getters and Setters =
	 * ======================= */

	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	public DateOption getOption() {
		return (DateOption) super.getOption();
	}

	@Override
	public DateOptionFormList getList() {
		return (DateOptionFormList) super.getList();
	}
	
	/**
	 * Updates {@link #startPicker} and {@link #endPicker} from the current {@link #timeZone} to the time zone given by the argument.
	 * This is meant to be called before a new time zone is set, so the pickers update accordingly.
	 * @param oldTimeZone the old time zone
	 * @param newTimeZone the new time zone.
	 */
	public void updateDateTimePickers(ZoneId oldTimeZone, ZoneId newTimeZone) {
		TimeUtils.updateDateTimePicker(startPicker, oldTimeZone, newTimeZone);
		TimeUtils.updateDateTimePicker(endPicker, oldTimeZone, newTimeZone);
	}
	
	/* =================
	 * = Data Handling =
	 * ================= */
	
	@Override
	protected void updateComponents() {
		super.updateComponents();
		if (location != null) location.setEnabled(!isDelete());
		if (location != null) startPicker.setEnabled(!isDelete());
		if (location != null) endPicker.setEnabled(!isDelete());
	}

	@Override
	public void loadData() {
		configureDebugLabel();
		binder.readBean(getOption());
	}
	
	@Override
	public boolean validate() {
		return binder.validate().isOk();
	}

	@Override
	public void writeIfValid() {
		binder.writeBeanIfValid(getOption());
	}

	/* ==========
	 * = Events =
	 * ========== */
	
	@Override
	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener) {
		super.addValueChangeListenerToFields(listener);
		HasValueFields.addValueChangeListenerToOnBlurTextField(location, listener);
		startPicker.addValueChangeListener(listener);
		endPicker.addValueChangeListener(listener);
	}

	
	public Registration addAddDateOptionEventListener(ComponentEventListener<AddDateOptionEvent> listener) { 
		return getEventBus().addListener(AddDateOptionEvent.class, listener);
	}
	
	/**
	 * Event indicating that a new {@link DateOption}/{@link DateOptionForm} shall be added to a {@link DateOptionFormList}.
	 * @author Andi Popp
	 *
	 */
	public static class AddDateOptionEvent extends ComponentEvent<DateOptionForm>{

		/**
		 * The option for which a form shall be added
		 */
		private DateOption option;

		/**
		 * Default constructor
		 * @param source the source {@link DateOptionForm}
		 * @param option value for {@link #option}
		 */
		public AddDateOptionEvent(DateOptionForm source, DateOption option) {
			super(source, false);
			this.option = option;
		}

		/**
		 * Getter for {@link #option}
		 * @return the {@link #option}
		 */
		public DateOption getOption() {
			return option;
		}	
	}
	
	/* =================
	 * = Other Methods =
	 * ================= */
	
	/**
	 * Create a new {@link DateOption} from the current inputs and an optional time offset in days. 
	 * Will return null if {@link #startPicker} or {@link #endPicker} have null values. 
	 * @param dayOffset a time offset for start and end date in days (can be 0).
	 * @return a new {@link DateOption} with data specified by the input fields, null if mandatory data is missing.
	 */
	public DateOption createFromFields(int dayOffset) {
		if (startPicker.getValue() == null || endPicker.getValue() == null) return null;
		DateOption option = new DateOption(startPicker.getValue().plusDays(dayOffset), endPicker.getValue().plusDays(dayOffset), getList().getTimezone());
		
		if (!title.getValue().isBlank()) option.setTitle(title.getValue());
		if (!location.getValue().isBlank()) option.setLocation(location.getValue());
		
		return option;
	}

	//Implementation of CalendarEvent
	
	@Override
	public String getUid() {
		return getOption().getId().toString();
	}

	@Override
	public Date getStart() {
		if (startPicker == null || startPicker.getValue() == null) return null;
		return Date.from(startPicker.getValue().atZone(getList().getTimezone()).toInstant());
	}

	@Override
	public Date getEnd() {
		if (endPicker == null || endPicker.getValue() == null) return null;
		return Date.from(endPicker.getValue().atZone(getList().getTimezone()).toInstant());
	}

	@Override
	public String getTitle() {
		return title.getValue();
	}

	@Override
	public String getLocation() {
		return location.getValue();
	}


}
