package de.andipopp.poodle.views.adminpanel;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("Admin Panel")
@Route(value = "admin-panel", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminPanelView extends VerticalLayout {

	Tabs tabs;
	
	Tab configTab = new Tab("Config");

	Tab pollTab = new Tab("Polls");

	PollsView pollsView;
	
	Tab userTab = new Tab("Users");
	
	VerticalLayout content = new VerticalLayout();
	
    public AdminPanelView(PollService pollService, UserService userService) {
    	this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	this.content.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	
    	pollsView = new PollsView(pollService);
    	this.setPadding(false);
    	this.setSpacing(false);
    	tabs = new Tabs(configTab, pollTab, userTab);
    	tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
    	tabs.addSelectedChangeListener(e -> setContent(e.getSelectedTab()));
    	tabs.setWidthFull();;
    	this.add(tabs, content);
    }
    
    private void setContent(Tab tab) {
    	this.content.removeAll();
    	if (tab.equals(pollTab)) this.content.add(pollsView);
    }

}
