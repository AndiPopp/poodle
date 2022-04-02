package de.andipopp.poodle.views.viewpoll;

import javax.swing.text.html.parser.ContentModel;

import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("View Poll")
@Route(value = "view-poll", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class ViewPollView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	PollService pollService;

	/**
	 * @param pollService
	 */
	public ViewPollView(PollService pollService) {
		super();
		this.pollService = pollService;
		
		for(AbstractPoll<?> poll : pollService.findAll()) {
			add(new Paragraph(poll.toString()));
		}
	}
	
    
    
//    public ViewPollView() {
//        setSpacing(false);
//
//        Image img = new Image("images/empty-plant.png", "placeholder plant");
//        img.setWidth("200px");
//        add(img);
//
//        add(new H2("This place intentionally left empty"));
//        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
//
//        setSizeFull();
//        setJustifyContentMode(JustifyContentMode.CENTER);
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        getStyle().set("text-align", "center");
//    }

}
