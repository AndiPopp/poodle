package de.andipopp.poodle.views.vote.simple;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.SimpleOption;
import de.andipopp.poodle.data.entity.polls.SimplePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.views.vote.OptionsVoteListView;

public class SimpleOptionsVoteListView extends OptionsVoteListView<SimplePoll, SimpleOption>  {

	public SimpleOptionsVoteListView(SimplePoll poll, User user, VoteService voteService, PollService pollService) {
		super(poll, user, voteService, pollService);
	}

}
