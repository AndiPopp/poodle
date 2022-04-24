package de.andipopp.poodle.views.mypolls;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("My Polls")
@Route(value = "mypolls", layout = MainLayout.class)
@PermitAll
public class MyPollsView extends VerticalLayout {

	PollService pollService;
	
	VoteService voteService;
	
	User currentUser;

	TextField titleFilterField = new TextField();

	private static final String MAX_LIST_WIDTH = "800px";
	
	private VerticalLayout pollList = new VerticalLayout();
	
	public MyPollsView(PollService pollService, VoteService voteService, AuthenticatedUser authenticatedUser) {
		//set date fields
		this.pollService = pollService;
		this.voteService = voteService;
		currentUser = null;
		if (!authenticatedUser.get().isEmpty()) currentUser = authenticatedUser.get().get();
		
		//configure compoenents
		updateList();
		pollList.setMaxWidth(MAX_LIST_WIDTH);
		pollList.setPadding(false);
		titleFilterField.setPlaceholder("Search by title...");
		titleFilterField.setMaxWidth(MAX_LIST_WIDTH);
		titleFilterField.setWidthFull();
		this.setFlexGrow(4, titleFilterField);
		titleFilterField.setValueChangeMode(ValueChangeMode.LAZY);
		titleFilterField.addValueChangeListener(e -> updateList());
		
		this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		this.add(titleFilterField, pollList);
		
	}
	
	private void updateList() {
		pollList.removeAll();
		for(AbstractPoll<?,?> poll : getPolls(titleFilterField.getValue())) {
			pollList.add(new PollSelectorBox(poll, currentUser));
		}
	}
	
	public Set<AbstractPoll<?,?>> getPolls(String titleFilter) {
		
		SortedSet<AbstractPoll<?,?>> result = new TreeSet<>((arg0, arg1) -> {
			if (arg0.getId().equals(arg1.getId())) return 0;
			int aux = arg1.getCreateDate().compareTo(arg0.getCreateDate());
			if (aux != 0) return aux;
			return arg0.getId().compareTo(arg1.getId());
		});
		
		//add all the polls owned by this user
		List<AbstractPoll<?, ?>> owned = pollService.findByOwner(currentUser);
		for(AbstractPoll<?,?> poll : owned) {
			if (poll.getTitle().toLowerCase().contains(titleFilter.strip().toLowerCase())) result.add(poll);
		}
		
		//add all the polls the user voted for
		List<Vote<?,?>> votes = voteService.findByOwner(currentUser);
		for(Vote<?,?> vote : votes) {
			AbstractPoll<?, ?> poll = vote.getParent();
			if (poll.getTitle().toLowerCase().contains(titleFilter.strip().toLowerCase())) result.add(poll);
		}
		
		return result;
		
		
	}
	
	
	
}
