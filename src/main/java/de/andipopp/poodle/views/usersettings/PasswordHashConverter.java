package de.andipopp.poodle.views.usersettings;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class PasswordHashConverter implements Converter<String, String> {

	public static final BCryptPasswordEncoder BCRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();
	
	@Override
	public Result<String> convertToModel(String clearPassword, ValueContext context) {
		String cypherPW = BCRYPT_PASSWORD_ENCODER.encode(clearPassword);
		return Result.ok(cypherPW);
	}

	@Override
	public String convertToPresentation(String value, ValueContext context) {
		return "";
	}

}
