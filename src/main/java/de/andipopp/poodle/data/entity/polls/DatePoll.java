package de.andipopp.poodle.data.entity.polls;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;

import biweekly.Biweekly;
import biweekly.ICalendar;
import de.andipopp.poodle.PoodleApplication;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.util.UUIDUtils;

@Entity
public class DatePoll extends AbstractPoll<DatePoll, DateOption> {
	
	public static final String TYPE_NAME = "DatePoll";
	
	/**
	 * Max length for {@link #location}
	 */
	public static final int MAX_LOCATION_LENGTH = 255;
	
	/**
	 * An optional location
	 */
	@Nullable
	@Column(length = MAX_LOCATION_LENGTH)
	@Size(max = MAX_LOCATION_LENGTH)
	private String location;
	
	/**
	 * Construct new poll with random UUID
	 */
	public DatePoll() {}

	/**
	 * @param title
	 * @param description
	 * @param location
	 */
	public DatePoll(@NotNull String title, String description, String location) {
		super(title, description);
		this.location = location;
	}

	/**
	 * @param owner
	 */
	public DatePoll(User owner) {
		super(owner);
	}
	
	/**
	 * Getter for {@link #location}
	 * @return the {@link #location}
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Setter for {@link #location}. Cleans dangerous HTML.
	 * @param location the {@link #location} to set
	 */
	public void setLocation(String location) {
		this.location = Jsoup.clean(location, Safelist.basic());
	}

	/**
	 * Build the product ID for this polls iCal product ID
	 * @return the product ID for this polls iCal product ID
	 */
	public String iCalProductID() {
		return "-//" +PoodleApplication.nameAndVersion() +  "//" + getTitle() +  "//EN"; //TODO Language support
	}
	
	@Override
	protected void setOptionParent(DateOption option) {
		option.setParent(this);
	}

	public String metaInf() {
		//First line is always the type identifier and the UUID
		String result = "DatePoll@" + getId().toString();
		//add the name
		if (getTitle() != null) result += "\r\nTitle: " + getTitle();
		//add location
		if (getLocation() != null) result += "\r\nLocation: "  + getLocation();
		
		return result;
	}
	
	@Override
	public String toString() {
		//start with the meta info
		String result = metaInf();
		//add the strings for the options
		for (Iterator<DateOption> it = getOptionIterator(); it.hasNext(); ) {
			result += "\r\n" + it.next().toString();
		}
		//return result
		return result;
	}

	/* ========================
	 * = UI auxiliary methods =
	 * ======================== */
	
	public Component winnerIcalAnchor() {  
		
		StreamResource res = new StreamResource(UUIDUtils.uuidToBase64url(getId())+".ics", (stream, session) -> {
			Biweekly.write(winnserToIcal()).go(stream);
			stream.close();
		});
		
        Anchor anchor = new Anchor(res, "");
        anchor.add(new Button("Results as iCalendar"));
        return anchor;
	}
	
	/* =================
	 * = Other methods =
	 * ================= */
	
	public List<DateOption> sortOptions() {
		getOptions().sort(new CalendarEventComparator());
		return getOptions();
	}
	
	public ICalendar winnserToIcal() {
		ICalendar calendar = new ICalendar();
		calendar.setProductId(iCalProductID());
		for (DateOption option : getOptions()) {
			if (option.isWinner()) calendar.addComponent(option.toVEvent());
		}
		return calendar;
	}
	
	
	
}
