package de.andipopp.poodle.views.poll;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.data.generator.NameGenerator;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.util.InvalidException;

public class OptionListView<P extends AbstractPoll<P, O>, O extends AbstractOption<P, O>> extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	protected PollService pollService;
	
	protected VoteService voteService;
	
	protected P poll;
	
	protected Vote<P,O> currentVote;
	
	/**
	 * The user using this view, if null we have an anonymous vote
	 */
	private User user;
	
	/**
	 * A new vote to store
	 */
	Vote<P,O> newVote;
	
	private Select<Vote<P,O>> voteSelector;
	
	protected HorizontalLayout header;
	
	Button saveButton = new Button("Save Vote");
	
	TextField displayNameInput = new TextField();
	
	/**
	 * @param poll
	 */
	public OptionListView(P poll, User user, VoteService voteService, PollService pollService) {
		//Set fields
		this.poll = poll;
		this.user = user;
		this.voteService = voteService;
		this.pollService = pollService;
		//configure layout
		this.setPadding(false);
		this.setSpacing(false);
		
		//build vote selector
		voteSelector = new Select<>();
		this.header = new HorizontalLayout();
//		this.header.getStyle().set("border", "2px dotted AntiqueWhite"); //for debug purposes
		this.header.setWidthFull();
		this.header.getStyle().set("margin-bottom", "1ex");
		header.add(voteSelector);
		this.add(header); 
	
		//configure
		Vote<P,O> usersVote = configureVoteSelector();
	
		//select the most likely vote, which triggers the set and build event
		if (usersVote == null) voteSelector.setValue(newVote);
		else voteSelector.setValue(usersVote);
		
		//hookup listener for save button
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addClickListener(e -> saveVote());
	}


	private boolean configureVoteSelectorMode = false;

	private Vote<P,O> configureVoteSelector() {
		configureVoteSelectorMode = true;
		List<Vote<P,O>> votes = new LinkedList<>();
		if (newVote == null) {
			newVote = new Vote<P,O>(this.poll);
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
	private void saveVote() {
		
		//Validate inputs
		displayNameInput.setValue(displayNameInput.getValue().strip());
		try {
			currentVote.validateAllAndSetDisplayName(displayNameInput.getValue());
		} catch (InvalidException e) {
			Notification notification = Notification.show(e.getMessage(), 4000, Notification.Position.BOTTOM_CENTER);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		//write the results to backend
		boolean result = false;
		String message = "Unexpected result while saving vote. Refresh to see if it worked.";
		if (currentVote.equals(newVote)) {
			this.poll.addVote(newVote);
			newVote.setOwner(user);
			newVote = null;
			result = pollService.update(poll) != null;
		} else if(currentVote.getOwner() == null || currentVote.getOwner().equals(user)) {
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
	
	public void loadVote(Vote<P,O> vote) {
		this.currentVote = vote;
		buildAll();
	}
	
	protected void buildAll() {
		buildList();
		buildFooter();
	}
	
	protected void clearList() {
		this.removeAll();
	}
	
	protected void buildList() {
		//start clean with only header
		this.clearList();
		this.add(header);
		
		//if we have a currrent vote, build the list
		if (currentVote != null) {
			for(AbstractOption<P,O> option : poll.getOptions()) {
				OptionListItem item = option.toOptionsListItem();
				item.loadVote(currentVote);
				item.build();
				this.add(item);
			}
		}
	}
	
	/**
	 * Builds the footer.
	 * Assumes a vote has been selected!
	 */
	protected void buildFooter() {
		if (poll.getWinners() == null || poll.getWinners().isEmpty()) buildSaveBar();
	}
	
	protected void buildSaveBar() {
		
		saveButton.setEnabled(true);
		if (currentVote.getOwner() != null) {
			saveButton.setEnabled(false);
			if (user.equals(currentVote.getOwner()) || user.equals(poll.getOwner())) saveButton.setEnabled(true);
		}
		
		configureDisplayNameInput();
		saveButton.setMinWidth("5em");
		HorizontalLayout saveBar = new HorizontalLayout(displayNameInput, saveButton);
		saveBar.setWidthFull();
//		saveBar.getStyle().set("border", "2px dotted FireBrick"); //for debug purposes
		saveBar.getStyle().set("margin-top", "1ex"); 
		saveBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		saveBar.setJustifyContentMode(JustifyContentMode.END);
		this.add(saveBar);
	}

	private void configureDisplayNameInput() {
		displayNameInput.setValue("");
		displayNameInput.setPlaceholder("Enter name");
		
		if (voteSelector.getValue().getDisplayName() != null) {
			displayNameInput.setValue(voteSelector.getValue().getDisplayName());
		}else if (voteSelector.getValue().getOwner() != null) {
			displayNameInput.setValue(voteSelector.getValue().getOwner().getName());
		}else if(voteSelector.getValue().equals(newVote)){
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
	
//	private boolean displayNameExists(String name) {
//		name = name.strip();
//		for(Iterator<Vote<P,O>> it = voteSelector.getListDataView().getItems().iterator(); it.hasNext();) {
//			if (it.next().getDisplayName().equals(name)) return true;
//		}
//		return false;
//	}

}
