package de.andipopp.poodle.views.poll.date;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.views.poll.OptionListBox;

/**
 * A box to display a single date option
 * @author Andi Popp
 *
 */
public class DateOptionListBox extends OptionListBox<DatePoll, DateOption> {

	
	
	public DateOptionListBox(AbstractOption<DatePoll, DateOption> option) {
		super(option);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	
}
