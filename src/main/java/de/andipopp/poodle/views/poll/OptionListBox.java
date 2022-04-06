package de.andipopp.poodle.views.poll;



import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Answer;

public class OptionListBox<P extends AbstractPoll<P, O>, O extends AbstractOption<P, O>> extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	
	AbstractOption<P,O> option;
	
	//Currently modified answer
	Answer<P, O> answer;

	HorizontalLayout content;
	
	/**
	 * @param option
	 */
	public OptionListBox(AbstractOption<P, O> option) {
		this.option = option;
		this.content = new HorizontalLayout();
		this.content.add(label());
		this.getStyle().set("border", "2px solid rgba(27, 43, 65, 0.69)");
		this.getStyle().set("background", "rgba(25, 59, 103, 0.05)");
		this.setSpacing(false);
		this.add(content, voteSummary());
	}
	
	protected Component label() {
		Span label = new Span(option.getTitle());
		label.getStyle().set("font-weight", "bold"); 
//		label.getStyle().set("border", "2px dotted DarkOrange"); //for debug purposes
		return label;
	}
	
	protected Component voteSummary() {
		int countYes = 0, countNeedBe = 0, countNo = 0;
		for(Answer<P, O> answer : option.getAnswers()) {
			switch (answer.getValue()){
			case YES:
				countYes++;
				break;
			case NO:
				countNo++;
				break;
			case IF_NEED_BE:
				countNeedBe++;
				break;
			default:
				break;
			}
		}

		Span result = new Span();
		result.getStyle().set("font-weight", "weight:bold");
		result.getStyle().set("-webkit-text-stroke", ".5px black");
		Span yes = new Span(""+countYes+" ");
		yes.getStyle().set("color", Answer.YES_COLOR);
		Image imgYes = new Image("images/VoteIcons-YesSquare.drawio.png", "in favor");
		imgYes.getStyle().set("width", "1em");
		imgYes.getStyle().set("height", "1em");
		imgYes.getStyle().set("vertical-align", "text-bottom");
		result.add(yes, imgYes);
		if (countNeedBe > 0) {
			Span ifNeedBe = new Span(" / "+countNeedBe+" ");
			ifNeedBe.getStyle().set("color", Answer.IF_NEED_BE_COLOR);
			Image imgNeedBe = new Image("images/VoteIcons-IfNeedBeSquare.drawio.png", "in favor if need be");
			imgNeedBe.getStyle().set("width", "1em");
			imgNeedBe.getStyle().set("height", "1em");
			imgNeedBe.getStyle().set("vertical-align", "text-bottom");
			result.add(ifNeedBe, imgNeedBe);
		}
		Span no = new Span(" / "+countNo+" ");
		no.getStyle().set("color", Answer.NO_COLOR);
		Image imgNo = new Image("images/VoteIcons-NoSquare.drawio.png", "opposed");
		imgNo.getStyle().set("width", "1em");
		imgNo.getStyle().set("height", "1em");
		imgNo.getStyle().set("vertical-align", "text-bottom");
		result.add(no, imgNo);
		return result;
	}
}
