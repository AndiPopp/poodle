package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.views.MainLayout;
import de.andipopp.poodle.views.PollView;

@PageTitle("Edit Poodle Poll")
@Route(value = "edit", layout = MainLayout.class)
@AnonymousAllowed
public class EditPollView extends PollView {

    private static final long serialVersionUID = 1L;

	public EditPollView(UserService userServer, PollService pollService) {
		super(userServer, pollService);
        setSpacing(false);

//        Image img = new Image("images/empty-plant.png", "placeholder plant");
        Image img = new Image();
        img.setAlt("Placeholder Plant");
        img.setSrc("images/empty-plant.png");
        img.setWidth("200px");
        add(img);

        add(new H2("This place intentionally left empty"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
            
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
	
	

}
