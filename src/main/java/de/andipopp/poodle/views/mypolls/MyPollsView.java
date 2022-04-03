package de.andipopp.poodle.views.mypolls;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
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
	
	ComboBox<String> stateCombobox = new ComboBox<String>("State");
	
    public MyPollsView(UserService userService, PollService pollService) {
    	String userName = VaadinRequest.getCurrent().getUserPrincipal().getName();
    	this.user = userService.get(userName);
    	this.pollService = pollService;
    	addClassName("myPolls-view");
//        setSizeFull();
    	setHeight("100%");
    	
    	boolean reduced = VaadinRequest.getCurrent().getHeader("user-agent").contains("Mobile");
    	
        configureGrid(reduced);
        add(grid);
        updateList();
    }

	private void updateList() {
//		grid.setItems(pollService.findByOwner(user));
		grid.setItems(pollService.findByOwnerNewest(user));
	}

	private void configureGrid(boolean reduced) {
		grid.addClassName("polls-grid");
		grid.removeAllColumns(); //empty out
		Grid.Column<AbstractPoll> titleCol = grid.addColumn(new ComponentRenderer<>(poll -> new GotoPollAnchor(poll)))
			.setHeader("My Polls")
			.setWidth("200px")
			.setFlexGrow(10);
		
		//for the reduced version we basically stop her //TODO make more appealing list for reduced version
		if (reduced) return;
		
		grid.setMaxWidth("1000px");
		titleCol.setHeader("Title").setComparator(AbstractPoll::getTitle);
		grid.addColumn(new LocalDateRenderer<>(
				AbstractPoll::getLocalCreateDate,
		        DateTimeFormatter.ofLocalizedDate(
		                FormatStyle.MEDIUM)))
		    .setHeader("Creation Date")
		    .setWidth("100px")
		    .setComparator(AbstractPoll::getCreateDate);
//		grid.addColumn("numberOfOptions");
		grid.addColumn("closed").setFlexGrow(0);
//		grid.addColumn("id"); //for debug purposes
//		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.addColumn(new ComponentRenderer<>(poll -> new EditPollButton(poll))).setFlexGrow(0);
		grid.addColumn(new ComponentRenderer<>(poll -> new GotoPollButtonAnchor(poll))).setFlexGrow(0);
	}

}
