package de.andipopp.poodle.views.editpoll.simple;

import java.util.ArrayList;

import de.andipopp.poodle.data.entity.polls.SimpleOption;
import de.andipopp.poodle.data.entity.polls.SimplePoll;
import de.andipopp.poodle.views.editpoll.AbstractOptionFormList;

public class SimpleOptionFormList extends AbstractOptionFormList<SimpleOptionForm> {

	public SimpleOptionFormList(SimplePoll poll) {
		super(poll);
	}
	
	@Override
	public SimplePoll getPoll() {
		return (SimplePoll) super.getPoll();
	}

	@Override
	protected void buildList() {
		setOptionForms(new ArrayList<>());
		for(SimpleOption option : getPoll().sortOptions()) {
			addSimpleOption(option);
		}
	}

	@Override
	public void addEmptyForm() {
		addSimpleOption(new SimpleOption());
	}
	
	/**
	 * Add a form to the given date option to the UI and the {@link #getOptionForms()} list.
	 * @param option the option to add a form for
	 */
	public void addSimpleOption(SimpleOption option) {
		SimpleOptionForm form = new SimpleOptionForm(option, this);
		form.buildAll();
		form.loadData();
		getOptionForms().add(form);
		addForm(form);
	}

	/**
	 * Align the options in this option forms list with the given {@link SimplePoll} bean.
	 * I.e. remove all options who are already in the poll but are marked to be deleted
	 * and add all options, including those which have been newly created.
	 * @param poll the poll bean to connect the options to.
	 */
	public void deleteAndConnect(SimplePoll poll) {
		for(SimpleOptionForm form : getOptionForms()) {
			if (form.isDelete()) {
				poll.removeOption(form.getOption());
			} else if (!poll.getOptions().contains(form.getOption())) {
				poll.addOption(form.getOption());
			}
		}
	}

}
