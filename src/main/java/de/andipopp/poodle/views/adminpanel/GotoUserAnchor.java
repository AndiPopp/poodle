package de.andipopp.poodle.views.adminpanel;

import com.vaadin.flow.component.html.Anchor;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.util.HtmlUtils;

public class GotoUserAnchor extends Anchor{
	
	User user;

	/**
	 * Construct a new follow
	 * @param user
	 */
	public GotoUserAnchor(User user) {
		this.user = user;
		this.setText(user.getUsername());
		this.setHref(HtmlUtils.linkToUser(user));
	}
}
