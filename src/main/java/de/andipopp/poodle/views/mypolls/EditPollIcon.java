package de.andipopp.poodle.views.mypolls;

import com.vaadin.flow.component.html.Span;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.views.MainLayout;

public class EditPollIcon extends Span {

	private AbstractPoll<?,?> poll;

	/**
	 * @param poll
	 */
	public EditPollIcon(AbstractPoll<?,?> poll) {
		this.poll = poll;
		this.setClassName("edit-poll-icon");
		this.getStyle().set("cursor", "pointer");
		this.add(new MainLayout.MenuItemInfo.LineAwesomeMenuIcon("la la-edit"));
//		this.addClickListener( e -> {
//			Notification.show("Edit "+poll.getTitle());
//		});
	}

	/**
	 * Getter for {@link #poll}
	 * @return the {@link #poll}
	 */
	public AbstractPoll<?,?> getPoll() {
		return poll;
	}
	
	
}
