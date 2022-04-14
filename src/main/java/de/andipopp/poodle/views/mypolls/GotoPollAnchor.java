package de.andipopp.poodle.views.mypolls;

import com.vaadin.flow.component.html.Anchor;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.util.HtmlUtils;

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
		this.setHref(HtmlUtils.linkToPoll(poll));
	}
	
	
	
}
