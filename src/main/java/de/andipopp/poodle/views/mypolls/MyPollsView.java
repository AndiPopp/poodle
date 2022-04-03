package de.andipopp.poodle.views.mypolls;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("My Polls")
@Route(value = "mypolls", layout = MainLayout.class)
@PermitAll
public class MyPollsView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	User user;
	
	PollService pollService;
	
	
	Grid<AbstractPoll> grid = new Grid<AbstractPoll>(AbstractPoll.class);
	TextField filterText = new TextField();
	
    public MyPollsView(UserService userService, PollService pollService) {
    	String userName = VaadinRequest.getCurrent().getUserPrincipal().getName();
    	this.user = userService.get(userName);
    	this.pollService = pollService;
    	addClassName("myPolls-view");
        setSizeFull();
    	
        configureGrid();
        
        add(grid);
        
        updateList();
        
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
    }

	private void updateList() {
		grid.setItems(pollService.findByOwner(user));
		
	}

	private void configureGrid() {
		grid.addClassName("polls-grid");
		grid.setSizeFull();
		grid.setColumns("title");
		grid.addColumn(new LocalDateTimeRenderer<>(
				AbstractPoll::getLocalCreateDate,
		        DateTimeFormatter.ofLocalizedDateTime(
		                FormatStyle.MEDIUM)))
		    .setHeader("Creation Date").setComparator(AbstractPoll::getCreateDate);
//		grid.addColumn("numberOfOptions");
		grid.addColumn("closed");
		grid.addColumn("id"); //for debug purposes
		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.addColumn(new ComponentRenderer<>(poll -> new EditPollButton(poll))).setFlexGrow(0);
		grid.addColumn(new ComponentRenderer<>(poll -> new GotoPollAnchor(poll))).setFlexGrow(0);
	}

}
