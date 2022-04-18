package de.andipopp.poodle.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.JSoupUtils;
import de.andipopp.poodle.views.mypolls.MyPollsView;

@PageTitle("Welcome to Poodle")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class WelcomeView extends VerticalLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;

    AuthenticatedUser authenticatedUser;
    
	public WelcomeView(AuthenticatedUser authenticatedUser) {
		
		this.authenticatedUser = authenticatedUser;
		
        setSpacing(false);

        add(new H1(Config.getCurrent().getWelcomeTitle()));
        add(new Html("<div>" + JSoupUtils.cleanBasicWithImages(Config.getCurrent().getWelcomeMessage()) + "</div>"));
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (!authenticatedUser.get().isEmpty()) {
			UI.getCurrent().navigate(MyPollsView.class);
			event.rerouteTo(MyPollsView.class);
			
		}
	}
}
