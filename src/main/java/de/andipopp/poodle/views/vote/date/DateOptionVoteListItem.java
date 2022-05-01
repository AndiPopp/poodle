package de.andipopp.poodle.views.vote.date;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import biweekly.ICalendar;
import de.andipopp.poodle.data.calendar.CalendarEvent;
import de.andipopp.poodle.data.calendar.CalendarEventConflicts;
import de.andipopp.poodle.data.calendar.TooManyCalendarEventsException;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.util.JSoupUtils;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.components.LineAwesomeIcon;
import de.andipopp.poodle.views.vote.OptionVoteListItem;

/**
 * A box to display a single date option
 * @author Andi Popp
 *
 */
public class DateOptionVoteListItem extends OptionVoteListItem {

	ZoneId zoneId = ZoneId.systemDefault();
	
	CalendarEventConflicts conflicts;
	
	UnorderedList hardConflictsList;
	
	UnorderedList softConflictsList;
	
	Details hardConflictHeader;
	
	Details softConflictHeader;
	
	Label hardConflictsLabel = new Label("Hard Conflicts");
	
	Label softConflictsLabel = new Label("Soft Conflicts");
	
	/**
	 * @param option
	 * @param vote
	 */
	public DateOptionVoteListItem(AbstractOption<?, ?> option, Vote<?, ?> vote, User currentUser) {
		super(option, vote, currentUser);
		subConstructor();
	}

	/**
	 * @param option
	 */
	public DateOptionVoteListItem(AbstractOption<?, ?> option, User currentUser) {
		super(option, currentUser);
		subConstructor();
	}

	/**
	 * Operations which are to be done by all constructors
	 */
	private void subConstructor() {
		this.setSpacing(false);
		
		hardConflictsList = new UnorderedList();
		hardConflictsList.getStyle().set("margin", "0px");
		hardConflictsList.getStyle().set("padding-left", "0em");
		
		LineAwesomeIcon red = new LineAwesomeIcon("la-exclamation-circle");
		red.getStyle().set("color", "var(--vote-color-no)");

		hardConflictHeader = new Details(new Div(red, hardConflictsLabel), hardConflictsList);
		hardConflictHeader.setWidthFull();
		hardConflictHeader.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.SMALL, DetailsVariant.FILLED);
		hardConflictHeader.addClassName("details-no-left-padding");
		
		
		softConflictsList = new UnorderedList();
		softConflictsList.getStyle().set("margin", "0px");
		softConflictsList.getStyle().set("padding-left", "0em");
		
		LineAwesomeIcon yellow = new LineAwesomeIcon("la-exclamation-circle");
		yellow.getStyle().set("color", "var(--vote-color-if-need-be)");
		
		softConflictHeader  = new Details(new Div(yellow, softConflictsLabel), softConflictsList);
		softConflictHeader.setWidthFull();
		softConflictHeader.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.SMALL, DetailsVariant.FILLED);
		softConflictHeader.addClassName("details-no-left-padding");
	}
	
	protected void updateConflicts() {
		//reset
		hardConflictsList.removeAll();
		softConflictsList.removeAll();
		
		//check if we have conflicts
		if (conflicts != null) {
			if(conflicts.hasHardConflicts()) {
				hardConflictsLabel.setText(" "+ conflicts.getHardConflicts().size() + " Hard Conflict(s)");
				for(CalendarEvent event : conflicts.getHardConflicts()) {
					hardConflictsList.add(new ListItem(event.format(zoneId)));
				}
			}
			if(conflicts.hasSoftConflicts()) {
				softConflictsLabel.setText(" "+ conflicts.getSoftConflicts().size() + " Soft Conflict(s)");
				for(CalendarEvent event : conflicts.getSoftConflicts()) {
					softConflictsList.add(new ListItem(event.format(zoneId)));
				}
			}
		}
	}
	
	
	@Override
	public void build() {
		super.build();
		VerticalLayout conflictLayout = new VerticalLayout();
		conflictLayout.setPadding(false);
		if (conflicts.hasHardConflicts()) conflictLayout.add(hardConflictHeader);
		if (conflicts.hasSoftConflicts()) conflictLayout.add(softConflictHeader);
		if (conflicts.hasHardConflicts() || conflicts.hasSoftConflicts())
			this.add(conflictLayout);
	}

	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	@Override
	protected DateOption getOption() {
		return (DateOption) super.getOption();
	}
	
	/**
	 * Getter for {@link #zoneId}
	 * @return the {@link #zoneId}
	 */
	protected ZoneId getZoneId() {
		return zoneId;
	}

	/**
	 * Setter for {@link #zoneId}
	 * @param zoneId the {@link #zoneId} to set
	 */
	protected void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}
	
	/**
	 * Getter for {@link #conflicts}
	 * @return the {@link #conflicts}
	 */
	public CalendarEventConflicts getConflicts() {
		return conflicts;
	}

	/**
	 * Setter for {@link #conflicts}
	 * @param conflicts the {@link #conflicts} to set
	 */
	public void setConflicts(CalendarEventConflicts conflicts) {
		this.conflicts = conflicts;
	}

	@Override
	protected String labelText() {
		String result = getOption().getZonedTimeStartEnd(zoneId);
		String connector = " (";
		if (getOption().getTitle() != null && !getOption().getTitle().isEmpty()) {
			result += connector + JSoupUtils.cleanNone(getOption().getTitle());
			connector = " / ";
		}
		if (getOption().getLocation() != null && !getOption().getLocation().isEmpty()) {
			result += connector + "<i class=\"las la-map-marker\"></i>" + JSoupUtils.cleanBasic(getOption().getLocation());
			connector = " / ";
		}
		if (connector.equals(" / ")) result += ")";
		return result;
	}

	@Override
	protected Component left() {
		VerticalLayout left = new VerticalLayout();
		left.setSpacing(false);
		left.setPadding(false);
//		left.getStyle().set("border", "2px dotted Red"); //for debug purposes
		Span monthDay = new Span(""+getOption().getZonedStartDay(zoneId));
		monthDay.getStyle().set("font-weight", "bold");
		monthDay.getStyle().set("font-size", "x-large");
		monthDay.getStyle().set("margin-bottom", "0px");
		monthDay.getStyle().set("margin-top", "0px");
//		monthDay.getStyle().set("border", "2px dotted Green"); //for debug purposes
		Span dayJumpMarker = null;
		long dayJumps = getOption().getZonedDayJumps(zoneId);
		if(dayJumps > 0) {
			dayJumpMarker = new Span("(+" + dayJumps +")");
			dayJumpMarker.getStyle().set("margin-bottom", "1ex");
		}
		Span bottom = new Span(getOption().getZonedStartWeekday(zoneId, null));
//		bottom.getStyle().set("border", "2px dotted Green"); //for debug purposes
		HorizontalLayout topWrapper = new HorizontalLayout(monthDay);
		if (dayJumpMarker != null) topWrapper.add(dayJumpMarker);
		topWrapper.setPadding(false);
		topWrapper.setSpacing(false);
		topWrapper.setDefaultVerticalComponentAlignment(Alignment.END);
		left.add(topWrapper, bottom);
		
		HorizontalLayout leftWrapper = new HorizontalLayout(left);
		leftWrapper.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		leftWrapper.setPadding(false);
		return leftWrapper;
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(VaadinUtils.getLocaleFromVaadinRequest());
	private static DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("E", VaadinUtils.getLocaleFromVaadinRequest());
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", VaadinUtils.getLocaleFromVaadinRequest());
	
	@Override
	protected String optionSummary() {
		String result = "";
		String connector = ", ";
		String currentConnector = "";
		if (getOption().getTitle() != null && !getOption().getTitle().isBlank()) {
			result += currentConnector + JSoupUtils.cleanNone(getOption().getTitle());
			currentConnector = connector;
		}
		
		result += currentConnector + weekDayFormatter.format(getOption().getZonedStart(zoneId));
		result += " " + formatter.format(getOption().getZonedStart(zoneId));
		long dayJumps = getOption().getZonedDayJumps(zoneId);
		if (dayJumps > 0) result += " (+" + dayJumps +")";
		result += " " + timeFormatter.format(getOption().getZonedStart(zoneId));
		currentConnector = connector;
				
		if (getOption().getLocation() != null && !getOption().getLocation().isBlank()) {
			result += currentConnector + getOption().getLocation();
			currentConnector = connector;
		}
				
		return result;
	}
	
	/**
	 * Loads conflicts for this item's option with a set of {@link ICalendar}s
	 * @param softConflictMinutes passed to {@link DateOption#checkConflicts(int, Collection, ZoneId)}
	 * @param iCalUrls passed to {@link DateOption#checkConflicts(int, Collection, ZoneId)}
	 * @throws TooManyCalendarEventsException from {@link DateOption#checkConflicts(int, Collection, ZoneId)}
	 * @throws IOException from {@link DateOption#checkConflicts(int, Collection, ZoneId)}
	 */
	protected void loadConflicts(int softConflictMinutes, Collection<String> iCalUrls) throws TooManyCalendarEventsException, IOException {
		this.conflicts = getOption().checkConflicts(softConflictMinutes, iCalUrls, zoneId);
	}
	
}
