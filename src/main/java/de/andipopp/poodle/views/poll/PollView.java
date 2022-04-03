package de.andipopp.poodle.views.poll;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.views.MainLayout;


@PageTitle("Poodle Poll")
@Route(value = "poll", layout = MainLayout.class)
@AnonymousAllowed
public class PollView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	
	PollService pollService;

	/**
	 * @param pollService
	 */
	public PollView(PollService pollService) {
		super();
		this.pollService = pollService;
	      setSpacing(false);
	
	      Image img = new Image("images/empty-plant.png", "placeholder plant");
	      img.setWidth("200px");
	      add(img);
	
	      add(new H2("This place intentionally left empty"));
	      add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
	
	      setSizeFull();
	      setJustifyContentMode(JustifyContentMode.CENTER);
	      setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	      getStyle().set("text-align", "center");		
	      
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		
	}

}