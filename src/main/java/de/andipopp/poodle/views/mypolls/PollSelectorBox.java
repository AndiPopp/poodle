package de.andipopp.poodle.views.mypolls;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.entity.polls.SimplePoll;
import de.andipopp.poodle.util.TimeUtils;
import de.andipopp.poodle.views.components.LineAwesomeIcon;

public class PollSelectorBox extends VerticalLayout{
	
	private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
	
	AbstractPoll<?,?> poll;

	public PollSelectorBox(AbstractPoll<?, ?> poll, User currentUser) {
		this.poll = poll;
		this.addClassName("text-box-button");
		
		
		Avatar avatar = poll.getAvatar();
		avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
		avatar.addClassName("text-box-button-text");
		avatar.getStyle().set("border", "1px solid var(--lumo-contrast-70pct)");
		Label titleLabel = new Label(poll.getTitle());
		titleLabel.addClassNames("text-box-button-text", "title");
		
		HorizontalLayout top = new HorizontalLayout(avatar, titleLabel);
		top.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		String createdLabelText = "Created " 
				+ poll.getCreateDate().atZone(TimeUtils.getUserTimeZone(currentUser)).format(isoFormatter);
		if (poll.getOwner() != null && poll.getOwner().getDisplayName() != null) createdLabelText += " by "+poll.getOwner().getDisplayName();
		if (poll.getVotes().size() > 0) createdLabelText += ", " + poll.getVotes().size() +" votes";
		Label createdLabel = new Label(createdLabelText + ".");
		createdLabel.addClassName("text-box-button-text");
		
		HorizontalLayout bottom = new HorizontalLayout(createdLabel, new iconLabel(poll));
		bottom.addClassName("text-box-button-text");
		bottom.setJustifyContentMode(JustifyContentMode.BETWEEN);
		bottom.setWidthFull();
		
		this.add(top, bottom);
		
		this.addClickListener(e -> UI.getCurrent().navigate("poll", poll.buildQueryParameters(false)));
	}
	
	public static class iconLabel extends Label{

		/**
		 * 
		 */
		public iconLabel(AbstractPoll<?,?> poll) {
			if (poll instanceof DatePoll) this.add(new LineAwesomeIcon("calendar"));
			if (poll instanceof SimplePoll) this.add(new LineAwesomeIcon("vote-yea"));
			if (poll.isClosed()) this.add(new LineAwesomeIcon("lock"));
			this.getStyle().set("font-size", "larger");
		}
	}
	
	
}
