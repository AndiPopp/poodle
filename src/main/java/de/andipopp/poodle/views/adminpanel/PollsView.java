package de.andipopp.poodle.views.adminpanel;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinRequest;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;

@RolesAllowed("ADMIN")
public class PollsView extends VerticalLayout {

	private static final String STATE_COMBOBOX_EITHER = "Open/Closed";
	
	private static final String STATE_COMBOBOX_OPEN = "Open";
	
	private static final String STATE_COMBOBOX_CLOSED = "Closed";
	
	private static final String MAX_CONTENT_WIDTH = "1000px";
	
	PollService pollService;

	Grid<AbstractPoll> grid = new Grid<>(AbstractPoll.class);
	
	TextField filterByTitle = new TextField();
	
	TextField filterByOwner = new TextField();
	
	ComboBox<String> stateCombobox;
	
	Span newIcon;

    public PollsView(PollService pollService) {
    	
    	this.pollService = pollService;
    	
    	addClassName("myPolls-view");
    	setHeightFull();
    	setMaxWidth(MAX_CONTENT_WIDTH);
    	
    	boolean reduced = VaadinRequest.getCurrent().getHeader("user-agent").contains("Mobile");
    	
        configureGrid(reduced);
        
        this.add(getToolbar(), grid);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setClassName("poll-grid");
        setPadding(false);
        this.setWidthFull();

        
        updateList();
    }


	private Component getToolbar() {
		
		
		filterByTitle.setPlaceholder("Filter by title ...");
		filterByTitle.setClearButtonVisible(true);
		filterByTitle.setValueChangeMode(ValueChangeMode.LAZY);
		filterByTitle.addValueChangeListener(e -> updateList());
		filterByTitle.setWidthFull();
		
		filterByOwner.setPlaceholder("Filter by owner ...");
		filterByOwner.setClearButtonVisible(true);
		filterByOwner.setValueChangeMode(ValueChangeMode.LAZY);
		filterByOwner.addValueChangeListener(e -> updateList());
		filterByOwner.setWidthFull();
		
		stateCombobox = new ComboBox<>(null, STATE_COMBOBOX_EITHER, STATE_COMBOBOX_OPEN, STATE_COMBOBOX_CLOSED);
		stateCombobox.setValue(STATE_COMBOBOX_EITHER);
		stateCombobox.addValueChangeListener(e -> updateList());
		stateCombobox.setWidth("9em");
		
		FormLayout toolbar = new FormLayout(filterByTitle, filterByOwner, stateCombobox);
		toolbar.setResponsiveSteps(
			new ResponsiveStep("0", 1),
			new ResponsiveStep("640px", 5)
		);
		toolbar.setColspan(filterByTitle, 2);
		toolbar.setColspan(filterByOwner, 2);
		toolbar.setColspan(stateCombobox, 1);
		return toolbar;
		
	}
	

	
	private void configureGrid(boolean reduced) {
		grid.addClassName("polls-grid");
		grid.removeAllColumns(); //empty out
		Grid.Column<AbstractPoll> titleCol = grid.addColumn(new ComponentRenderer<>(poll -> new GotoPollAnchor(poll)))
			.setHeader("Title")
			.setComparator(AbstractPoll::getTitle)
			.setWidth("250px")
			.setFlexGrow(6)
		;
		
		Grid.Column<AbstractPoll> ownerCol = grid.addColumn(new ComponentRenderer<>(poll -> {
			if (poll.getOwner() != null && poll.getOwner().getDisplayName() != null) return new Label(poll.getOwner().getDisplayName());
			else return new Label("");
		}))
				.setHeader("Owner")
				.setWidth("150px")
				.setFlexGrow(1)
				.setComparator((arg0, arg1) -> {
					if (arg0.getOwner() == null && arg1.getOwner() == null) return 0;
					if (arg0.getOwner() != null && arg1.getOwner() == null) return -1;
					if (arg0.getOwner() == null && arg1.getOwner() != null) return 1;
					String displayName0 = "";
					String displayName1 = "";
					if (arg0.getOwner().getDisplayName() != null) displayName0 = arg0.getOwner().getDisplayName();
					if (arg1.getOwner().getDisplayName() != null) displayName1 = arg1.getOwner().getDisplayName();
					return displayName0.compareTo(displayName1);
				})
		;
			
		
		//Reduced version does not get those//TODO make more appealing list for reduced version
		if (!reduced) {
			grid.addColumn(new LocalDateRenderer<>(
					AbstractPoll::getLocalCreateDate,
			        DateTimeFormatter.ofLocalizedDate(
			                FormatStyle.MEDIUM)))
			    .setHeader("Creation Date")
			    .setWidth("150px")
			    .setFlexGrow(0)
			    .setComparator(AbstractPoll::getCreateDate);
			grid.addColumn("closed").setFlexGrow(0);
		}
		
		//Reduced version gets that
		Grid.Column<AbstractPoll> editCol = grid.addColumn(new ComponentRenderer<>(poll -> {
			EditPollIcon icon = new EditPollIcon(poll);
			RouterLink link = poll.linkToEdit();
			link.add(icon);
			return link;
		})).setWidth("3em");
		if (!reduced) editCol.setFlexGrow(0);
	}


	
	private void updateList() {
		Boolean closed = null;
		if (stateCombobox.getValue() != null) {
			if (stateCombobox.getValue().equals(STATE_COMBOBOX_CLOSED)) {
				closed = true;
			}
			else if (stateCombobox.getValue().equals(STATE_COMBOBOX_OPEN)) {
				closed = false;
			}
		}
		grid.setItems(pollService.findNewest(filterByTitle.getValue(), filterByOwner.getValue(), closed));

	}



}
