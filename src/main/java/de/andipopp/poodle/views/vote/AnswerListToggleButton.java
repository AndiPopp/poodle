package de.andipopp.poodle.views.vote;

import com.vaadin.flow.component.html.Image;

import de.andipopp.poodle.data.entity.polls.Answer;
import de.andipopp.poodle.data.entity.polls.AnswerType;
import de.andipopp.poodle.util.InvalidException;

public class AnswerListToggleButton extends Image{

	/**
	 * The answers whose value is to be changed
	 */
	Answer<?,?> answer;

	/**
	 * The value of this button
	 */
	AnswerType value;
	
	
	public AnswerListToggleButton(Answer<?, ?> answer) {
		this.answer = answer;
		this.value = answer.getValue();
		this.addClassName("toggle");
		this.setMinHeight("3ex");
		this.setMaxHeight("5ex");
		this.addClickListener(e -> toggleValue());
		loadImage();
	}
	
	public void validateAnswer() throws InvalidException {
		answer.validate(value);
	}
	
	public void readValueToAnswer() {
		answer.setValue(value);
	}
	
	private void toggleValue() {
		value = (value.nextAnswer(
				answer.getOption().getParent().isEnableAbstain(), 
				answer.getOption().getParent().isEnableIfNeedBe()));
		loadImage();
	}
	
	private void loadImage() {
		switch (value) {
		case YES:
			this.setSrc("images/VoteIcons-Yes.drawio.png");
			this.setAlt("Yes");
			break;
		case IF_NEED_BE:
			this.setSrc("images/VoteIcons-IfNeedBe.drawio.png");
			this.setAlt("If Need Be");
			break;
		case NO:
			this.setSrc("images/VoteIcons-No.drawio.png");
			this.setAlt("No");
			break;
		default:
			this.setSrc("images/VoteIcons-None.drawio.png");
			this.setAlt("Abstain");
			break;
		}
	}

	
	
}
