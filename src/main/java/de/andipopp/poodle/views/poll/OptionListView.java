package de.andipopp.poodle.views.poll;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Vote;

public abstract class OptionListView<P extends AbstractPoll<P, O>, O extends AbstractOption<P, O>> extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private P poll;
	
	private Vote<P,O> currentVote;
	
	private Select<Vote<P,O>> voteSelector;

	private HorizontalLayout header;
	
	/**
	 * @param poll
	 */
	public OptionListView(P poll) {
		this.poll = poll;
		voteSelector = new Select<>();
		voteSelector.setItems(poll.getVotes());
		voteSelector.setItemLabelGenerator(vote -> {
			if (vote.getOwner() != null) return vote.getOwner().getName();
			if (vote.getDisplayName() != null) return vote.getDisplayName();
			return "Unknown user";
		});
		voteSelector.setLabel("Edit other vote");
		this.header = new HorizontalLayout();
		header.add(voteSelector);
		this.add(header); 
	}

	/**
	 * Getter for {@link #poll}
	 * @return the {@link #poll}
	 */
	public P getPoll() {
		return poll;
	}

	/**
	 * Setter for {@link #poll}
	 * @param poll the {@link #poll} to set
	 */
	public void setPoll(P poll) {
		this.poll = poll;
	}

	/**
	 * Getter for {@link #voteSelector}
	 * @return the {@link #voteSelector}
	 */
	public Select<Vote<P, O>> getVoteSelector() {
		return voteSelector;
	}

	/**
	 * Setter for {@link #voteSelector}
	 * @param voteSelector the {@link #voteSelector} to set
	 */
	public void setVoteSelector(Select<Vote<P, O>> voteSelector) {
		this.voteSelector = voteSelector;
	}
	
	public void loadVoate();
	
	protected abstract void buildList();
	
}
