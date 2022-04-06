package de.andipopp.poodle.views.poll.date;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.views.poll.OptionListItem;

/**
 * A box to display a single date option
 * @author Andi Popp
 *
 */
public class DateOptionListBox extends OptionListItem {

	
	
	

	/**
	 * @param option
	 * @param vote
	 */
	public DateOptionListBox(AbstractOption<DatePoll, DateOption> option, Vote<DatePoll, DateOption> vote) {
		super(option, vote);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	
}
