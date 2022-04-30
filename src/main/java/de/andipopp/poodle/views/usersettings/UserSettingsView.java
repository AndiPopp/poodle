package de.andipopp.poodle.views.usersettings;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.NotAUuidException;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("User Settings")
@Route(value = "user-settings", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class UserSettingsView extends VerticalLayout implements BeforeEnterObserver {

	/**
	 * Max width at which the layout looks pleasing
	 */
	private static final String MAX_CONTENT_WIDTH = "1000px"; 
	
	public static final String ID_PARAM_NAME = "userId";
	
	private UserService userService;
	
	private VoteService voteService;
	
	private PollService pollService;
	
	private AuthenticatedUser authenticatedUser;

	private User editedUser;
	
	private Tabs tabs;

	private Tab generalTab;
	
	GeneralUserSettingsView generalUserSettingsView;
	
	private Tab datePollTab;
	
	DatePollSettingsView datePollSettingsView;
	
	VerticalLayout content = new VerticalLayout();
	
	/**
	 * @param userService
	 * @param authenticatedUser
	 */
	public UserSettingsView(UserService userService, AuthenticatedUser authenticatedUser, VoteService voteService, PollService pollService) {
		this.userService = userService;
		this.pollService = pollService;
		this.voteService = voteService;
		this.authenticatedUser = authenticatedUser;
		if (!authenticatedUser.get().isEmpty()) editedUser = authenticatedUser.get().get(); //default to the user editing themselves
		
		this.setPadding(false);
		this.setSpacing(false);
		
		generalTab = new Tab("General");
		datePollTab = new Tab("Date Polls");
		tabs = new Tabs(generalTab, datePollTab);
		tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
		tabs.setWidthFull();
		tabs.addSelectedChangeListener(e -> setContent(e.getSelectedTab()));
		
		content.setMaxWidth(MAX_CONTENT_WIDTH);
		this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		this.add(tabs, content);
		
	}
	
    private void setContent(Tab tab) {
    	this.content.removeAll();
    	if (tab.equals(generalTab)) this.content.add(generalUserSettingsView); 
    	else if (tab.equals(datePollTab)) this.content.add(datePollSettingsView);
    }

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (event.getLocation().getQueryParameters().getParameters().containsKey(ID_PARAM_NAME)) {
			//only admins may edit users via id
			if (!authenticatedUserIsAdmin()) {
				this.removeAll();
				this.add(new Label("Access denied"));
				return;
			}
			
			try {
				UUID id = UUIDUtils.base64urlToUuid(
					event.getLocation().getQueryParameters().getParameters().get(ID_PARAM_NAME).get(0)
				);
				Optional<User> editedUser = userService.get(id);
				if (editedUser.isEmpty()) throw new NotAUuidException("Unknown UUID "+id.toString());
				this.editedUser = editedUser.get();
			} catch (NotAUuidException e) {
				this.removeAll();
				this.add(new Label(e.getMessage()));
				return;
			}	
		}
		
		generalUserSettingsView = new GeneralUserSettingsView(editedUser, authenticatedUser, userService, voteService, pollService);
		datePollSettingsView = new DatePollSettingsView(authenticatedUser, editedUser, userService);
		tabs.setSelectedTab(datePollTab);
		tabs.setSelectedTab(generalTab);
	}
	
	private boolean authenticatedUserIsAdmin() {
		return isAdmin(authenticatedUser);
	}
	
	public static boolean isAdmin(AuthenticatedUser authenticatedUser) {
		return !authenticatedUser.get().isEmpty() && authenticatedUser.get().get().getRoles().contains(Role.ADMIN);
	}
	

}
