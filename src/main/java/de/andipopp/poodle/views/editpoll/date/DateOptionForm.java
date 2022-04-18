package de.andipopp.poodle.views.editpoll.date;

import java.time.ZoneId;
import java.util.Date;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.views.HasValueFields;
import de.andipopp.poodle.views.editpoll.AbstractOptionForm;

/**
 * Layout to edit {@link DateOption}s.
 * It add the necessary input fields for {@link DateOption} specific fields, time zone support and 
 * a few convenience features for adding new options.
 * @author Andi Popp
 *
 */
public class DateOptionForm extends AbstractOptionForm {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	//DateOption specific components
	
	/**
	 * Binder to handle the text fields, which do not need time zone support
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
	
	/* =============================
	 * = Constructors and Builders =
	 * ============================= */
	
	/**
	 * Construct a new form with the given arguments
	 * @param option the value for {@link #getOption()}
	 * @param timeZone the value for {@link #timeZone}
	 */
	public DateOptionForm(DateOption option, DateOptionFormList list) {
		//call the super-constructor and set the data fields
		super(option, list);
		
		//configure the specific UI components
		endPicker.setRequiredIndicatorVisible(true);
		startPicker.setRequiredIndicatorVisible(true);
		location.setRequired(false);
		title.setRequired(false);
		
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
	protected void buildButtonBar() {
		super.buildButtonBar();
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

	public void loadData() {
		configureDebugLabel();
		binder.readBean(getOption());
	}
	
	public boolean validate() {
		return binder.validate().isOk();
	}

	public void writeIfValid() {
		binder.writeBeanIfValid(getOption());
	}

	@Override
	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener) {
		super.addValueChangeListenerToFields(listener);
		HasValueFields.addValueChangeListenerToOnBlurTextField(location, listener);
		startPicker.addValueChangeListener(listener);
		endPicker.addValueChangeListener(listener);
	}

}
