package de.andipopp.poodle.views.poll.date;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.poll.PollListView;

public class DatePollListView extends PollListView<DatePoll, DateOption> {

	private static final long serialVersionUID = 1L;

	private ZoneId zoneId;

	private ComboBox<ZoneId> zoneIdSelector;
	
	public DatePollListView(DatePoll poll, User user, VoteService voteService, PollService pollService) {
		super(poll, user, voteService, pollService);
		
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
		if (getPoll().isClosed()) header.add(getPoll().winnerIcalAnchor());
	}

	private final static DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM Y");
	
	
	
	@Override
	protected void buildHeader() {
		super.buildHeader();
		if (zoneIdSelector != null) {
			header.add(zoneIdSelector);
			if (getPoll().isClosed()) header.add(getPoll().winnerIcalAnchor());
		}
	}

	@Override
	protected void buildList() {
		getPoll().sortOptions();
		
		//start clean with only header
		this.clearList();
		this.add(header);
		
		//remember the month;
		ZonedDateTime lastStart = null;
		
		//if we have a current vote, build the list
		if (currentVote != null) {
			if (zoneId == null) zoneId = ZoneId.systemDefault(); //this should normally be handled by the zone selector
			
			for(DateOption option : poll.getOptions()) {
				//check if we have a new month
				ZonedDateTime currentStart = option.getZonedStart(zoneId);
				if (lastStart == null 
						|| lastStart.getYear() != currentStart.getYear() 
						|| !lastStart.getMonth().equals(currentStart.getMonth())) {
					Component monthSeparator = new H4(monthFormatter.format(currentStart));
					this.add(monthSeparator);
				}
				lastStart = currentStart;
				
				//Construct the list item
				DateOptionListItem item = option.toOptionsListItem(getUser());
				item.setZoneId(zoneId);
				configureItemAndaddToList(item);
			}
		}
	}	
	
	
}
