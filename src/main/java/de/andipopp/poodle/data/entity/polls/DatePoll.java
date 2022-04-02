package de.andipopp.poodle.data.entity.polls;

import javax.persistence.Entity;

@Entity
public class DatePoll extends AbstractPoll<DateOption> {

	@Override
	protected void setParent(DateOption option) {
		option.setParent(this);
	}



}
