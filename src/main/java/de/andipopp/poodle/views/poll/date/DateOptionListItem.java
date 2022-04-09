package de.andipopp.poodle.views.poll.date;

import java.time.ZoneId;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.DateOption;
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

	ZoneId zoneId = ZoneId.systemDefault();
	
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

	@Override
	protected String labelText() {
		String result = getOption().getZonedTimeStartEnd(zoneId);
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
	
}
