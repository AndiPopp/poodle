package de.andipopp.poodle.views.mypolls;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;

public class EditPollButton extends Button {

	AbstractPoll<?> poll;

	/**
	 * @param poll
	 */
	public EditPollButton(AbstractPoll<?> poll) {
		this.poll = poll;
		this.setText("Edit");
		this.addClickListener( e -> {
			Notification.show("Edit "+poll.getTitle());
		});
	}
	
	
}
