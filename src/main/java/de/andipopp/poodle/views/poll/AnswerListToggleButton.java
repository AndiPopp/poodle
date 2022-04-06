package de.andipopp.poodle.views.poll;

import com.vaadin.flow.component.html.Image;

import de.andipopp.poodle.data.entity.polls.Answer;

public class AnswerListToggleButton extends Image{

	private static final long serialVersionUID = 1L;

	OptionListItem parent;
	
	Answer<?,?> answer;

	/**
	 * @param parent
	 * @param answer
	 */
	public AnswerListToggleButton(OptionListItem parent, Answer<?, ?> answer) {
		this.parent = parent;
		this.answer = answer;
		this.setMinHeight("5ex");
		this.setMaxHeight("7ex");
//		this.getStyle().set("width", "5ex");
//		this.getStyle().set("height", "5ex");
		loadImage();
	}
	
	private void loadImage() {
		switch (answer.getValue()) {
		case YES:
			this.setSrc("images/VoteIcons-Yes.drawio.png");
			this.setAlt("Yes");
			break;
		case IF_NEED_BE:
			this.setSrc("images/VoteIcons-IfNeedBe.drawio.png");
			this.setAlt("Yes");
			break;
		case NO:
			this.setSrc("images/VoteIcons-No.drawio.png");
			this.setAlt("Yes");
			break;
		default:
			this.setSrc("images/VoteIcons-None.drawio.png");
			this.setAlt("Yes");
			break;
		}
	}

	
	
}
