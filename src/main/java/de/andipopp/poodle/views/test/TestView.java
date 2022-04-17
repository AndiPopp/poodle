package de.andipopp.poodle.views.test;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("View Poll")
@Route(value = "view-poll", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class TestView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	PollService pollService;

	/**
	 * @param pollService
	 */
	public TestView(PollService pollService) {
		super();
		this.pollService = pollService;
		
		for(AbstractPoll<?,?> poll : pollService.findAll()) {
			add(new Paragraph(poll.toString()));
		}
		
		this.add(testBox("320px"));
		this.add(testBox("480px"));
		this.add(testBox("640px"));
		this.add(testBox("800px"));
		this.add(testBox("1080px"));
	}
	
	private HorizontalLayout testBox(String width) {
		HorizontalLayout horizontalLayout = new HorizontalLayout(new Label(width));
		horizontalLayout.setWidth(width);
		horizontalLayout.getStyle().set("border", "2px dotted FireBrick");
		return horizontalLayout;
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
