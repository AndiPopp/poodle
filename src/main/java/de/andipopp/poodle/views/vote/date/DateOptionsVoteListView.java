package de.andipopp.poodle.views.vote.date;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

import de.andipopp.poodle.data.calendar.CalendarEventComparator;
import de.andipopp.poodle.data.calendar.CalendarEventConflicts;
import de.andipopp.poodle.data.calendar.TooManyCalendarEventsException;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.views.components.TextFieldList;
import de.andipopp.poodle.views.components.ZoneIdComboBox;
import de.andipopp.poodle.views.vote.OptionsVoteListView;

public class DateOptionsVoteListView extends OptionsVoteListView<DatePoll, DateOption> {

	private ZoneId zoneId;

	private ZoneIdComboBox zoneIdSelector;
	
	private TextFieldList icsPaths = new TextFieldList();
	
	private final Map<DateOption, CalendarEventConflicts> conflicts = new TreeMap<>(new CalendarEventComparator(true));
	
	public DateOptionsVoteListView(DatePoll poll, User user, VoteService voteService, PollService pollService) {		
		super(poll, user, voteService, pollService);
		
		zoneId = TimeUtils.getUserTimeZone(getUser());
		icsPaths.setValue(user.getIcsPaths());
		loadConflicts();
		
		//configure the selector with the given zoneId and add the listener afterwards, so we do not fire the event here
		zoneIdSelector = new ZoneIdComboBox(zoneId);
		zoneIdSelector.addValueChangeListener(e -> {
			zoneId = zoneIdSelector.getValue();
			loadConflicts();
			buildAll();
		});
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
			
			/* This is still a dirty work-around for because the constructor of PollListView triggers load vote 
			 * (and therefore buildAll and buildList) by setting the value for the vote selector, all of it before
			 * the constructor of this class has a change to set the time zone.
			 * In general, the problem is that both selector should trigger a rebuild, but the rebuild only makes
			 * sense once a vote and a zoneId have been selected.
			 */
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
				DateOptionVoteListItem item = option.toOptionsListItem(getUser());
				item.setZoneId(zoneId);
				if (conflicts != null && conflicts.containsKey(option)) {
					item.setConflicts(conflicts.get(option));
					item.updateConflicts();
				}
				configureItemAndaddToList(item);
			}
		}
	}

	public void loadConflicts() {
		conflicts.clear();
		for(DateOption option : poll.getOptions()) {
			int softConflictMinutes = 4*60;
			if (getUser() != null) softConflictMinutes = getUser().getSoftConflictMinutes();
			try {
				conflicts.put(option, option.checkConflicts(softConflictMinutes, icsPaths.getValue(), zoneId));
			} catch (TooManyCalendarEventsException | IOException e) {
				Notification.show("Error loading conflicts:\n"+e.getMessage(), 7500, Position.BOTTOM_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
				conflicts.clear();
			} 
		}
	
	}
}
