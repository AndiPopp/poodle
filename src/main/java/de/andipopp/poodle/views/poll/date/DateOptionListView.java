package de.andipopp.poodle.views.poll.date;

import java.time.ZoneId;

import com.vaadin.flow.component.select.Select;

import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.poll.OptionListView;

public class DateOptionListView extends OptionListView<DatePoll, DateOption> {

	private static final long serialVersionUID = 1L;

	private ZoneId zoneId;

	private Select<ZoneId> zoneIdSelector;
	
	/**
	 * @param poll
	 * @param zoneId
	 */
	public DateOptionListView(DatePoll poll, ZoneId zoneId) {
		super(poll);
		if (zoneId == null) this.zoneId = VaadinUtils.guessTimeZoneFromVaadinRequest();
		else this.zoneId = zoneId;
		
		
	}
	
	
	
}
