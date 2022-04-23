package de.andipopp.poodle.views.usersettings;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("User Settings")
@Route(value = "user-settings", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class UserSettingsView extends Tabs {

	private UserService userService;
	
	private AuthenticatedUser authenticatedUser;

	/**
	 * @param userService
	 * @param authenticatedUser
	 */
	public UserSettingsView(UserService userService, AuthenticatedUser authenticatedUser) {
		super();
		this.userService = userService;
		this.authenticatedUser = authenticatedUser;
		
		this.addThemeVariants(TabsVariant.LUMO_CENTERED);
		
		this.add(new Tab("General"));
		this.add(new Tab("Date Polls"));
		
	}
	
	

}
