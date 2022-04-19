package de.andipopp.poodle.views.editpoll.date;

import java.time.LocalDateTime;
import java.util.Date;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.LocalDateTimeToDateConverter;

/**
 * A wrapper for {@link LocalDateTimeToDateConverter} which uses variable time zones from a {@link DateOptionFormList}.
 * @author Andi Popp
 *
 */
public class VariableLocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
	
	/**
	 * The list to take the time zone from
	 */
	final DateOptionFormList list;
	
	/**
	 * Construct a new converter for the given list
	 * @param list value for {@link #list}
	 */
	public VariableLocalDateTimeToDateConverter(DateOptionFormList list) {
		this.list = list;
	}

	@Override
	public Result<Date> convertToModel(LocalDateTime value, ValueContext context) {
		LocalDateTimeToDateConverter converter = new LocalDateTimeToDateConverter(list.getTimezone());
		return converter.convertToModel(value, context);
	}

	@Override
	public LocalDateTime convertToPresentation(Date value, ValueContext context) {
		LocalDateTimeToDateConverter converter = new LocalDateTimeToDateConverter(list.getTimezone());
		return converter.convertToPresentation(value, context);
	}

	
}
