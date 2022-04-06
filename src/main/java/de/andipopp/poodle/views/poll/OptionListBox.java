package de.andipopp.poodle.views.poll;



import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
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
		String result = "", connector = "";
		if (countYes > 0) {
			result += "<span style=\"font-weight:bold; -webkit-text-stroke:.5px black; color:" + Answer.YES_COLOR + "\">" + countYes + "</span> in favor"; 
			connector = ", ";
		}
		if (countNeedBe > 0) {
			result += connector + "<span style=\"font-weight:bold; -webkit-text-stroke:.5px black; color:" + Answer.IF_NEED_BE_COLOR + "\">" + countNeedBe + "</span> in favor if need be"; 
			connector = ", ";
		}
		if (countNo > 0) {
			result += connector + "<span style=\"font-weight:bold; -webkit-text-stroke:.5px black; color:" + Answer.NO_COLOR + "\">" + countNo + "</span>  opposed"; 
		}
		result += ".";
		
		return new Html("<span>"+result+"</span>");
	}
	
	//The version below looks nice and I am not really willing to trash it, yet. But it does take a lot of screen real estate
	
//	protected Component voteSummary() {
//		AvatarGroup yes = new AvatarGroup();
//		yes.setMaxItemsVisible(MAX_AVATARS);
//		AvatarGroup maybe = new AvatarGroup();
//		maybe.setMaxItemsVisible(MAX_AVATARS);
//		for(Answer<P, O> answer : option.getAnswers()) {
//			AvatarGroupItem item = VaadinUtils.avatarToGroupItem(answer.getVote().getAvatar());
//			if (answer.getValue() == AnswerType.YES) yes.add(item);
//			if (answer.getValue() == AnswerType.IF_NEED_BE) maybe.add(item);
//		}
//		yes.addThemeVariants(AvatarGroupVariant.LUMO_XSMALL);
//		maybe.addThemeVariants(AvatarGroupVariant.LUMO_XSMALL);
//		Label yesLabel = new Label(""+yes.getItems().size());
//		yesLabel.getStyle().set("color", Answer.YES_COLOR);
//		yesLabel.getStyle().set("font-size", "xx-large");
//		yesLabel.getStyle().set("font-weight", "bold");
//		yesLabel.getStyle().set("-webkit-text-stroke", "1px black");
//		HorizontalLayout container = new HorizontalLayout(yesLabel, new Html("<span>&nbsp;</span>"), yes);
//		if (!maybe.getItems().isEmpty()) {
//			Label maybeLabel = new Label("  +"+maybe.getItems().size());
//			maybeLabel.getStyle().set("color", Answer.IF_NEED_BE_COLOR);
//			maybeLabel.getStyle().set("font-size", "xx-large");
//			maybeLabel.getStyle().set("font-weight", "bold");
//			maybeLabel.getStyle().set("-webkit-text-stroke", "1px black");
//			container.add(new Html("<span>&nbsp;&nbsp;</span>"), maybeLabel, new Html("<span>&nbsp;</span>"), maybe);
//		}
//		container.setDefaultVerticalComponentAlignment(Alignment.CENTER);
//		container.setSpacing(false); //we space ourselves
//		container.getStyle().set("border", "2px dotted DarkOrange"); //for debug purposes
//		return container;
//	}
	
	
}
