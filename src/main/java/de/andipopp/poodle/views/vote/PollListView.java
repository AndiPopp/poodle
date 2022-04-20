package de.andipopp.poodle.views.vote;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.data.generator.NameGenerator;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.util.InvalidException;

public abstract class PollListView<P extends AbstractPoll<P, O>, O extends AbstractOption<P, O>> extends VerticalLayout {


	protected PollService pollService;
	
	protected VoteService voteService;
	
	protected P poll;
	
	protected Vote<P,O> currentVote;
	
	
	/**
	 * The user using this view, if null we have an anonymous vote
	 */
	private User user;
	
	private boolean closingMode = false;
	
	/**
	 * A new vote to store
	 */
	Vote<P,O> newVote;
	
	private Select<Vote<P,O>> voteSelector;
	
	protected HorizontalLayout header;

	protected List<OptionListItem> optionListItems = new LinkedList<>();
	
	Button saveButton = new Button("Save");
	
	Button deleteButton = new Button("Delete");
	
	TextField displayNameInput = new TextField();
	
	Button closeButton = new Button("Save and Close Poll");

	public PollListView(P poll, User user, VoteService voteService, PollService pollService) {
		//Set fields
		this.poll = poll;
		this.user = user;
		this.voteService = voteService;
		this.pollService = pollService;

		//configure layout
		this.setPadding(false);
		this.setSpacing(false);
		this.setMaxWidth("800px");

		this.header = new HorizontalLayout();
		this.header.setWidthFull();
		this.header.getStyle().set("margin-bottom", "1ex");
		this.header.setDefaultVerticalComponentAlignment(Alignment.END);
		this.add(header); 
		
		//build vote selector
		voteSelector = new Select<>();
		Vote<P,O> usersVote = configureVoteSelector();
		guessVote(usersVote);
		
		//hookup listener for buttons
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> saveCurrentVote());
		
		deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		deleteButton.addClickListener(e -> confirmDeleteVote());
		
		closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		closeButton.addClickListener(e -> closePoll());
	}

	/**
	 * Getter for {@link #poll}
	 * @return the {@link #poll}
	 */
	public P getPoll() {
		return poll;
	}

	/**
	 * Setter for {@link #poll}
	 * @param poll the {@link #poll} to set
	 */
	public void setPoll(P poll) {
		this.poll = poll;
	}
	
	/**
	 * Getter for {@link #user}
	 * @return the {@link #user}
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Setter for {@link #user}
	 * @param user the {@link #user} to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Getter for {@link #closingMode}
	 * @return the {@link #closingMode}
	 */
	public boolean isClosingMode() {
		return closingMode;
	}

	/**
	 * Setter for {@link #closingMode}
	 * @param closingMode the {@link #closingMode} to set
	 */
	public void setClosingMode(boolean closingMode) {
		this.closingMode = closingMode;
		buildAll();
	}

	public void toggleClosingMode() {
		setClosingMode(!closingMode);
	}
	
	/**
	 * Getter for {@link #voteSelector}
	 * @return the {@link #voteSelector}
	 */
	public Select<Vote<P, O>> getVoteSelector() {
		return voteSelector;
	}

	/**
	 * Setter for {@link #voteSelector}
	 * @param voteSelector the {@link #voteSelector} to set
	 */
	public void setVoteSelector(Select<Vote<P, O>> voteSelector) {
		this.voteSelector = voteSelector;
	}

	private void guessVote(Vote<P, O> usersVote) {
		//select the most likely vote, which triggers the set and build event
		if (usersVote == null) voteSelector.setValue(newVote);
		else voteSelector.setValue(usersVote);
	}


	private boolean configureVoteSelectorMode = false;

	private Vote<P,O> configureVoteSelector() {
		configureVoteSelectorMode = true;
		List<Vote<P,O>> votes = new LinkedList<>();
		
		if (newVote == null) {
			newVote = new Vote<P,O>(this.poll, false);
		}
		votes.add(newVote); //add the default new vote
		
		ArrayList<Vote<P,O>> sortedVotes = new ArrayList<>();
		sortedVotes.addAll(poll.getVotes());
		sortedVotes.sort((v1, v2) -> v1.getListLabel().compareTo(v2.getListLabel()));
		
		//add the other votes to the selector and use the loop to find the user's vote if present
		Vote<P,O> usersVote = null;
		for(Vote<P,O> vote : sortedVotes) {
			votes.add(vote);
			if (vote.getOwner() != null && vote.getOwner().equals(user)) usersVote = vote;
		}
//		System.out.println("Loaded "+(sortedVotes.size()+1)+" votes");
		voteSelector.setItems(votes);
		voteSelector.setItemLabelGenerator(v -> {
			if (v.getOwner() == null) return v.getListLabel();
			if (v.getOwner().equals(user)) return v.getListLabel()+" ★";
			return v.getListLabel()+" ☆";
		});
		voteSelector.setLabel("Select vote");
		voteSelector.addValueChangeListener(e -> {
			if (!configureVoteSelectorMode) loadVote(e.getValue());
		});
		voteSelector.setMaxWidth("10em");
		
		configureVoteSelectorMode = false;
		return usersVote;
	}


	/**
	 * Save the current vote
	 */
	private void saveCurrentVote() {
		
		//Validate inputs
		displayNameInput.setValue(displayNameInput.getValue().strip());
		try {
			//validate the input fields
			currentVote.validateDisplayName(displayNameInput.getValue());
			for(OptionListItem item : optionListItems) item.validateAnswerFromButton();
			//read values into the object
			currentVote.setDisplayName(displayNameInput.getValue());
			for(OptionListItem item : optionListItems) item.readAnswerFromButton();
		} catch (InvalidException e) {
			Notification notification = Notification.show(e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		//TODO The rest down here needs to be cleaned up
		
//		Logger logger = LoggerFactory.getLogger(getClass());
    	
		//write the results to backend
		boolean result = false;
		String message = "Unexpected result while saving vote. Refresh to see if it worked.";
		if (currentVote.equals(newVote)) {
//			logger.info("Adding to "+voteService.count()+" votes, new vote "+currentVote.getId());
			//hookup the new vote to the poll, its answers to the options and declare the owner
			this.poll.addVote(currentVote);
			currentVote.hookupAnswersToOptions();
			currentVote.setOwner(user);
			
			//write write the new vote to the db and get the id from the result
			Vote<?,?> repResult = voteService.update(currentVote);
			currentVote.setId(repResult.getId());
			
			newVote = null;
			result = repResult != null;
		} else 
		if(currentVote.getOwner() == null || currentVote.getOwner().equals(user)) {
//			logger.info("Modifying in "+voteService.count()+" votes, vote "+currentVote.getId());
			result = voteService.update(currentVote) != null;
		} else {
			message = "You don't have permission to edit this vote.";
			result = false;
		}
		
		if (result) {
			Notification notification = Notification.show("Vote saved", 2000, Notification.Position.BOTTOM_CENTER);
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			poll.clearSortedOptionsByPositiveAnswers();
			Vote<P,O> currentAux = currentVote;
			configureVoteSelector();
			voteSelector.setValue(currentAux);
		}else {
			Notification notification = Notification.show(message);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
//		logger.info("Now having "+voteService.count()+" votes.");
	}
	
	private void confirmDeleteVote() {
		//let user confirm delete via dialogue
		Dialog confirm = new Dialog();
		confirm.setModal(true);
		
		Button ok = new Button("OK");
		ok.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		ok.addClickListener(e -> {
			confirm.close();
			deleteCurrentVote();
		});
		Button cancel = new Button("Cancel");
		cancel.addClickListener(e -> confirm.close());
		cancel.addThemeName("secondary");
		
		HorizontalLayout buttonLayout = new HorizontalLayout(cancel, ok);
		Label label = new Label("Delete vote of "+currentVote.getDisplayName());
		
		VerticalLayout dialogLayout = new VerticalLayout(label, buttonLayout);
		
		confirm.add(dialogLayout);
		confirm.open();
		
	}
	
	private void deleteCurrentVote() {
		poll.removeVote(currentVote);
		voteService.delete(currentVote); //first time removes the connections
		voteService.delete(currentVote); //second time removes echo
		guessVote(configureVoteSelector());
	}
	
	private void closePoll() {
		for(OptionListItem item : optionListItems) item.readWinnerFromButton();
		poll.setClosed(true);
		pollService.update(poll);
		setClosingMode(false);
		buildAll();
		fireEvent(new ViewChangeEvent(this));
	}
	
	public void loadVote(Vote<P,O> vote) {
		this.currentVote = vote;
		buildAll();
	}
	
	protected void buildAll() {
		buildHeader();
		buildList();
		buildFooter();
	}
	
	protected void buildHeader() {
		header.removeAll();
		if (!poll.isClosed() && !closingMode) header.add(voteSelector);
	}
	
	protected void clearList() {
		this.removeAll();
		this.optionListItems.clear();
	}
	
	protected void buildList() {
		//start clean with only header
		this.clearList();
		this.add(header);
		//if we have a currrent vote, build the list
		if (currentVote != null) {
			for(AbstractOption<P,O> option : poll.getOptions()) {
				OptionListItem item = option.toOptionsListItem(user);
				configureItemAndaddToList(item);
			}
		}
	}

	protected void configureItemAndaddToList(OptionListItem item) {
		item.setClosingMode(closingMode);
		item.loadVote(currentVote);
		item.build();
		optionListItems.add(item);
		this.add(item);
	}

	/**
	 * Builds the footer.
	 * Assumes a vote has been selected!
	 */
	protected void buildFooter() {
		if (!poll.isClosed() && !closingMode) buildSaveBar();
		else if(!poll.isClosed() && closingMode) buildCloseBar();
	}
	
	protected void buildCloseBar() {
		closeButton.setEnabled(user != null && user.equals(poll.getOwner()));
		HorizontalLayout closeBar = buildFooterBar(closeButton);
		this.add(closeBar);
	}
	
	protected void buildSaveBar() {
		
		//configure button enabling
		saveButton.setEnabled(true);
		deleteButton.setEnabled(true);
		displayNameInput.setEnabled(true);
		if (currentVote.getOwner() != null) {
			saveButton.setEnabled(currentVote.canEdit(user));
			deleteButton.setEnabled(currentVote.canEdit(user));
			displayNameInput.setEnabled(currentVote.canEdit(user));
		}
		if (currentVote.equals(newVote)) deleteButton.setEnabled(false);
		
		configureDisplayNameInput();
		HorizontalLayout saveBar = buildFooterBar(deleteButton, displayNameInput, saveButton);
		this.add(saveBar);
	}

	private void configureDisplayNameInput() {
		displayNameInput.setValue("");
		displayNameInput.setPlaceholder("Enter name");
		displayNameInput.setMaxWidth("10em");
		
		if (voteSelector.getValue().getDisplayName() != null) {
			displayNameInput.setValue(voteSelector.getValue().getDisplayName());
		}else if (voteSelector.getValue().getOwner() != null) {
			displayNameInput.setValue(voteSelector.getValue().getOwner().getName());
		}

		else if(voteSelector.getValue().equals(newVote)){
			if (user != null) {
				displayNameInput.setValue(user.getName());
			}
		}
		
		try {
			if (!displayNameInput.getValue().isEmpty()) voteSelector.getValue().validateDisplayName(displayNameInput.getValue());
		} catch (InvalidException e) {
			displayNameInput.setValue(displayNameInput.getValue()+" "+NameGenerator.randomNumberLabel(3));
		}
	}
	
	private HorizontalLayout buildFooterBar(Component ... comps) {
		HorizontalLayout footerBar = new HorizontalLayout(comps);
		footerBar.setWidthFull();
		footerBar.getStyle().set("margin-top", "1ex"); 
		footerBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		footerBar.setJustifyContentMode(JustifyContentMode.END);
		return footerBar;
	}
	
	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) { 
		return getEventBus().addListener(eventType, listener);
	}
	
	public static class ViewChangeEvent extends ComponentEvent<PollListView<?, ?>>{
		
		public ViewChangeEvent(PollListView<?, ?> source) {
			super(source, false);
		}
	}
}
