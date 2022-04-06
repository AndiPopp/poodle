package de.andipopp.poodle.views.poll.date;

import java.time.ZoneId;

import com.vaadin.flow.component.HtmlComponent;

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
public class DateOptionListItem extends OptionListItem {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param option
	 * @param vote
	 */
	public DateOptionListItem(AbstractOption<?, ?> option, Vote<?, ?> vote) {
		super(option, vote);
	}

	/**
	 * @param option
	 */
	public DateOptionListItem(AbstractOption<?, ?> option) {
		super(option);
	}

	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	@Override
	protected DateOption getOption() {
		return (DateOption) super.getOption();
	}
	
	@Override
	protected String labelText() {
		//TODO take zone id from UI
		String result = getOption().getZonedTimeStartEnd(ZoneId.systemDefault());
		String connector = " (";
		if (getOption().getTitle() != null && !getOption().getTitle().isEmpty()) {
			result += connector + getOption().getTitle();
			connector = " / ";
		}
		if (getOption().getLocation() != null && !getOption().getLocation().isEmpty()) {
			result += connector + "@" + getOption().getLocation(); //TODO replace with proper location icon
			connector = " / ";
		}
		if (connector.equals(" / ")) result += ")";
		return result;
	}
	

	
}
