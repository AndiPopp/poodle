package de.andipopp.poodle.views.poll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.cookieconsent.CookieConsent.Position;
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
import de.andipopp.poodle.data.service.VoteService;

public class OptionListView<P extends AbstractPoll<P, O>, O extends AbstractOption<P, O>> extends VerticalLayout {

	private static final long serialVersionUID = 1L;

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
	public OptionListView(P poll, User user, VoteService voteService) {
		//Set fields
		this.poll = poll;
		this.user = user;
		this.voteService = voteService;
		//configure layout
		this.setPadding(false);
		this.setSpacing(false);
		//build vote selector
		voteSelector = new Select<>();
		Vote<P, O> usersVote = configureVoteSelector(poll, user);
		
		this.header = new HorizontalLayout();
//		this.header.getStyle().set("border", "2px dotted AntiqueWhite"); //for debug purposes
		this.header.setWidthFull();
		this.header.getStyle().set("margin-bottom", "1ex");
		header.add(voteSelector);
		this.add(header); 
		
		//hookup listener for save button
		saveButton.addClickListener(e -> saveVote());
		
		//select the most likely vote, which triggers the set and build event
		if (usersVote == null) voteSelector.setValue(newVote);
		else voteSelector.setValue(usersVote);
	}



	private Vote<P, O> configureVoteSelector(P poll, User user) {
		List<Vote<P,O>> votes = new LinkedList<>();
		if (newVote == null) {
			newVote = new Vote<P,O>(this.poll);
		}
		votes.add(newVote); //add the default new vote
		
		ArrayList<Vote<P,O>> sortedVotes = new ArrayList<>();
		sortedVotes.addAll(poll.getVotes());
		sortedVotes.sort((v1, v2) -> v1.getDisplayName().compareTo(v2.getDisplayName()));
		
		//add the other votes to the selector and use the loop to find the user's vote if present
		Vote<P,O> usersVote = null;
		for(Vote<P,O> vote : sortedVotes) {
			votes.add(vote);
			if (vote.getOwner() != null && vote.getOwner().equals(user)) usersVote = vote;
		}
		System.out.println("Loaded "+(sortedVotes.size()+1)+" votes");
		voteSelector.setItems(votes);
		voteSelector.setItemLabelGenerator(Vote::getDisplayName);
		voteSelector.setLabel("Select vote");
		voteSelector.addValueChangeListener(e -> loadVote(e.getValue()));
		voteSelector.setMaxWidth("10em");
		return usersVote;
	}


	
	private void saveVote() {
		
		if (displayNameInput.getValue() == null || displayNameInput.getValue().isEmpty()) {
			Notification notification = Notification.show("Enter a name!", 2000, Notification.Position.BOTTOM_CENTER);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		
		currentVote.setAnonymousName(displayNameInput.getValue());
		
		if (voteService.update(currentVote) != null) {
			Notification notification = Notification.show("Vote saved", 2000, Notification.Position.BOTTOM_CENTER);
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			//if we stored a new vote, set the newVote field to null
			if (currentVote.equals(newVote)) newVote = null; 
		}else {
			Notification notification = Notification.show("Unexpected result while saving vote. Refresh to see if it worked.");
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
	
	protected void buildFooter() {
		displayNameInput.setValue("");
		if (voteSelector.getValue().getOwner() != null) {
			displayNameInput.setValue(voteSelector.getValue().getOwner().getName());
			displayNameInput.setEnabled(false);
		}else if(voteSelector.getValue().equals(newVote)){
			if (user != null) {
				displayNameInput.setValue(user.getName());
				displayNameInput.setEnabled(false);
			}
		}else if(voteSelector.getValue().getAnonymousName() != null) {
			displayNameInput.setValue(voteSelector.getValue().getAnonymousName());
			displayNameInput.setEnabled(true);
		}else {
			displayNameInput.setValue(""); 
			displayNameInput.setEnabled(true);
		}
		
		HorizontalLayout saveBar = new HorizontalLayout(displayNameInput, saveButton);
		saveBar.setWidthFull();
//		saveBar.getStyle().set("border", "2px dotted FireBrick"); //for debug purposes
		saveBar.getStyle().set("margin-top", "1ex"); 
		saveBar.setDefaultVerticalComponentAlignment(Alignment.END);
		saveBar.setJustifyContentMode(JustifyContentMode.END);
		this.add(saveBar);
		
		
	}
}
