package de.andipopp.poodle.views.poll.date;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import com.vaadin.flow.component.combobox.ComboBox;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.poll.OptionListView;

public class DateOptionListView extends OptionListView<DatePoll, DateOption> {

	private static final long serialVersionUID = 1L;

	private ZoneId zoneId;

	private ComboBox<ZoneId> zoneIdSelector;
	
	/**
	 * @param poll
	 * @param zoneId
	 */
	public DateOptionListView(DatePoll poll, User user) {
		super(poll, user);
		
		if (user != null && user.getTimeZone() != null) {
			this.zoneId = user.getTimeZone();
		} else {
			this.zoneId = VaadinUtils.guessTimeZoneFromVaadinRequest(); 
		}
		zoneIdSelector = new ComboBox<>();
		zoneIdSelector.setItems(TimeUtils.getSupportedZoneIdsInAlphabeticalOrder());
		zoneIdSelector.setValue(zoneId);
		zoneIdSelector.setLabel("Time Zone");
		zoneIdSelector.addValueChangeListener(e -> {
			zoneId = zoneIdSelector.getValue();
			buildAll();
		});
		zoneIdSelector.setMaxWidth("50%");
		zoneIdSelector.setItemLabelGenerator(z -> z.getDisplayName(TextStyle.NARROW, Locale.US) + " " + z.getRules().getOffset(Instant.now()));
		
		header.add(zoneIdSelector);
	}

	@Override
	protected void buildList() {
		//start clean with only header
		this.clearList();
		this.add(header);
		
		//if we have a currrent vote, build the list
		if (currentVote != null) {
			for(DateOption option : poll.getOptions()) {
				DateOptionListItem item = option.toOptionsListItem();
				if (zoneId != null) item.setZoneId(zoneId);
				item.loadVote(currentVote);
				item.build();
				this.add(item);
			}
		}
	}	
	
	
}
