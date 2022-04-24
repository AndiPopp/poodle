package de.andipopp.poodle.views.adminpanel;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.service.ConfigService;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("Admin Panel")
@Route(value = "admin-panel", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminPanelView extends VerticalLayout {

	Tabs tabs;
	
	Tab configTab = new Tab("Config");

	ConfigView configView;
	
	Tab pollTab = new Tab("Polls");

	PollsView pollsView;
	
	Tab usersTab = new Tab("Users");
	
	UsersView usersView;
	
	VerticalLayout content = new VerticalLayout();
	
    public AdminPanelView(PollService pollService, UserService userService, ConfigService configService) {
    	this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	this.content.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	this.setHeightFull();
    	this.content.setHeightFull();
    	
    	pollsView = new PollsView(pollService);
    	configView = new ConfigView(configService);
    	usersView = new UsersView(userService);
    	
    	this.setPadding(false);
    	this.setSpacing(false);
    	tabs = new Tabs(configTab, pollTab, usersTab);
    	tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
    	tabs.addSelectedChangeListener(e -> setContent(e.getSelectedTab()));
    	tabs.setWidthFull();;
    	this.add(tabs, content);
    	tabs.setSelectedIndex(1);
    }
    
    private void setContent(Tab tab) {
    	this.content.removeAll();
    	if (tab.equals(pollTab)) this.content.add(pollsView);
    	else if (tab.equals(configTab)) this.content.add(configView);
    	else if (tab.equals(usersTab)) this.content.add(usersView);
    }

}
