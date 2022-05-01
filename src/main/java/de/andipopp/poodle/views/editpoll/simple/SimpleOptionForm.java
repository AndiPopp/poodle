package de.andipopp.poodle.views.editpoll.simple;

import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.polls.SimpleOption;
import de.andipopp.poodle.views.editpoll.AbstractOptionForm;

public class SimpleOptionForm extends AbstractOptionForm {
	
	/**
	 * Binder to for input fields data handling
	 */
	private Binder<SimpleOption> binder = new BeanValidationBinder<>(SimpleOption.class);
	
	public SimpleOptionForm(SimpleOption option, SimpleOptionFormList list) {
		//call the super-constructor to set the bean and parent list form
		super(option, list);
		
		//bind the the fields
		binder.bindInstanceFields(this);
	}

	@Override
	public SimpleOption getOption() {
		return (SimpleOption) super.getOption();
	}

	@Override
	public SimpleOptionFormList getList() {
		return (SimpleOptionFormList) super.getList();
	}

	@Override
	public void loadData() {
		configureDebugLabel();
		binder.readBean(getOption());
	}

	@Override
	public boolean validate() {
		return binder.validate().isOk();
	}

	@Override
	public void writeIfValid() {
		binder.writeBeanIfValid(getOption());
	}

}
