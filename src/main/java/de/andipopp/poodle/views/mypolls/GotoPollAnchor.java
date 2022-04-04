package de.andipopp.poodle.views.mypolls;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.poll.PollView;

public class GotoPollAnchor extends Anchor{

	private static final long serialVersionUID = 1L;
	
	AbstractPoll<?,?> poll;

	/**
	 * Construct a new follow
	 * @param poll
	 */
	public GotoPollAnchor(AbstractPoll<?,?> poll) {
		this.poll = poll;
		this.setText(poll.getTitle());
		RouterLink link = new RouterLink("Test", PollView.class);
		this.setHref(link.getHref()+"?pollId="+UUIDUtils.uuidToBase64url(poll.getId()));
	}
	
	
	
}
