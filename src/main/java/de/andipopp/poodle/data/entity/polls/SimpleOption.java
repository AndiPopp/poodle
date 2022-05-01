package de.andipopp.poodle.data.entity.polls;

import javax.persistence.Entity;

/**
 * Option for a {@link SimplePoll}
 * @author Andi Popp
 *
 */
@Entity
public class SimpleOption extends AbstractOption<SimplePoll, SimpleOption> {

	@Override
	protected void setThisOption(Answer<SimplePoll, SimpleOption> answer) {
		answer.setOption(this);
	}

	/**
	 * Create new empty option
	 */
	public SimpleOption() {
		super();
	}

	/**
	 * Create an option with the given title
	 * @param title value for {@link #getTitle()}
	 */
	public SimpleOption(String title) {
		super(title);
	}
	
	

}
