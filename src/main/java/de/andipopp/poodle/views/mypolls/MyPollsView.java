package de.andipopp.poodle.views.mypolls;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
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
	
	ComboBox<String> stateCombobox;
	
	private static final String STATE_COMBOBOX_EITHER = "Open/Closed";
	
	private static final String STATE_COMBOBOX_OPEN = "Open";
	
	private static final String STATE_COMBOBOX_CLOSED = "Closed";
	
	private static final String WIDTH = "1000px";
	
    public MyPollsView(UserService userService, PollService pollService) {
    	String userName = VaadinRequest.getCurrent().getUserPrincipal().getName();
    	this.user = userService.get(userName);
    	this.pollService = pollService;
    	addClassName("myPolls-view");
//        setSizeFull();
    	setHeight("100%");
    	setMaxWidth(WIDTH);
    	
    	boolean reduced = VaadinRequest.getCurrent().getHeader("user-agent").contains("Mobile");
    	
        configureGrid(reduced);
        add(
        	getToolbar(reduced),
        	grid
        );
        updateList();
    }

	private Component getToolbar(boolean reduced) {
		filterText.setPlaceholder("Filter by title ...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
		filterText.setWidthFull();
		
		stateCombobox = new ComboBox<>(null, STATE_COMBOBOX_EITHER, STATE_COMBOBOX_OPEN, STATE_COMBOBOX_CLOSED);
		stateCombobox.setValue(STATE_COMBOBOX_EITHER);
		stateCombobox.addValueChangeListener(e -> updateList());
		stateCombobox.setWidth("9em");
		
//		Button addContactButton = new Button("Add contact");
		HorizontalLayout toolbar = new HorizontalLayout(filterText, stateCombobox);
		toolbar.setWidthFull();
		return toolbar;
	}

	private void updateList() {
		if (filterText.isEmpty() && (stateCombobox.getValue() == null || stateCombobox.getValue().equals(""))) {
			grid.setItems(pollService.findNewestByOwner(user));
		} else {
			Boolean closed = null;
			if (stateCombobox.getValue() != null) {
				if (stateCombobox.getValue().equals(STATE_COMBOBOX_CLOSED)) {
					closed = true;
				}
				else if (stateCombobox.getValue().equals(STATE_COMBOBOX_OPEN)) {
					closed = false;
				}
			}
			grid.setItems(pollService.findNewestByOwner(user, filterText.getValue(), closed));
		}
	}

	private void configureGrid(boolean reduced) {
		grid.addClassName("polls-grid");
		grid.removeAllColumns(); //empty out
		Grid.Column<AbstractPoll> titleCol = grid.addColumn(new ComponentRenderer<>(poll -> new GotoPollAnchor(poll)))
			.setHeader("My Polls")
			.setWidth("200px")
			.setFlexGrow(8);
		
		//for the reduced version we basically stop her //TODO make more appealing list for reduced version
		if (reduced) return;
		
//		grid.setMaxWidth(WIDTH);
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
		grid.addColumn(new ComponentRenderer<>(poll -> {
			if (poll.getOwner().equals(user)) return new EditPollButton(poll);
			return new Label("");
		})).setFlexGrow(0);
//		grid.addColumn(new ComponentRenderer<>(poll -> new GotoPollButtonAnchor(poll))).setFlexGrow(0);
	}

}
