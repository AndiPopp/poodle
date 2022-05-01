package de.andipopp.poodle.views.vote.simple;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.views.vote.OptionVoteListItem;

public class SimpleOptionVoteListItem extends OptionVoteListItem {

	public SimpleOptionVoteListItem(AbstractOption<?, ?> option, User currentUser) {
		super(option, currentUser);
	}

	public SimpleOptionVoteListItem(AbstractOption<?, ?> option, Vote<?, ?> vote, User currentUser) {
		super(option, vote, currentUser);
	}



}
