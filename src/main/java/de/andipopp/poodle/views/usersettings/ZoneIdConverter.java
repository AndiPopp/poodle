package de.andipopp.poodle.views.usersettings;

import java.time.ZoneId;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ZoneIdConverter implements Converter<ZoneId, String> {

	@Override
	public Result<String> convertToModel(ZoneId value, ValueContext context) {
		return Result.ok(value.toString());
	}

	@Override
	public ZoneId convertToPresentation(String value, ValueContext context) {
		return ZoneId.of(value);
	}

	

}
