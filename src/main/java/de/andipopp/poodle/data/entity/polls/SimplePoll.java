package de.andipopp.poodle.data.entity.polls;

import javax.persistence.Entity;

import de.andipopp.poodle.data.entity.User;

/**
 * A simple poll with only the basic features of {@link AbstractPoll}.
 * @author Andi Popp
 *
 */
@Entity
public class SimplePoll extends AbstractPoll<SimplePoll, SimpleOption> {

	public static final String TYPE_NAME = "SimplePoll";
	
	/**
	 * Create new empty poll
	 */
	public SimplePoll() {}

	/**
	 * Create poll with given title and description
	 * @param title value for {@link #getTitle()}
	 * @param description value for {@link #getDescription()}
	 */
	public SimplePoll(String title, String description) {
		super(title, description);
	}

	/**
	 * Construct a new empty poll for the given user
	 * @param owner value for {@link #getOwner()}
	 */
	public SimplePoll(User owner) {
		super(owner);
	}

	@Override
	protected void setOptionParent(SimpleOption option) {
		option.setParent(this);
	}

}
