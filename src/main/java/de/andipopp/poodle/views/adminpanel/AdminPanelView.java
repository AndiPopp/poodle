package de.andipopp.poodle.views.adminpanel;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.views.MainLayout;

@PageTitle("Admin Panel")
@Route(value = "admin-panel", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminPanelView extends Tabs {

	Tab configTab;
	
	Tab pollTab;
	
	Tab userTab;
	
    public AdminPanelView() {
    	configTab = new Tab("Config"); 
    	pollTab = new Tab("Polls");	
    	userTab = new Tab("Users");
    	this.add(configTab, pollTab, userTab);
    	this.addThemeVariants(TabsVariant.LUMO_CENTERED);
    	
    }

}
