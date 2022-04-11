package de.andipopp.poodle.views.poll;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.Answer;
import de.andipopp.poodle.data.entity.polls.Vote;

public class OptionListItem extends HorizontalLayout {

	private static final long serialVersionUID = 1L;
	
	private AbstractOption<?,?> option;
	
	private User currentUser;
	
	private boolean closingMode;
	
	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	protected AbstractOption<?, ?> getOption() {
		return option;
	}

	/**
	 * Currently modified vote
	 */
	Vote<?, ?> vote;
	
	/**
	 * The toggle button to switch the answer
	 */
	AnswerListToggleButton answerToggleButton;
	
	/**
	 * The toggle button to select the winner
	 */
	OptionSelectToggleButton winnerToggleButton;

	public OptionListItem(AbstractOption<?, ?> option, User currentUser) {
		//set fields
		this.currentUser = currentUser;
		this.option = option;
		
		//format according to state
		this.addClassName("optionListBox");
		
		this.removeClassNames("winningOption", "losingOption");
		if (option.getParent().isClosed()) {
			if (option.isWinner()) {
				this.addClassName("winningOption");
			} else {
				this.addClassName("losingOption");
			}
		} else {
			if (option.isPotentialWinnerByPositiveVotes()) this.addClassName("potentialWinner");
		}
		
		//general format
		this.setPadding(true);
		this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		this.setWidthFull();
	}


	public OptionListItem(AbstractOption<?, ?> option, Vote<?,?> vote, User currentUser) {
		this(option, currentUser);
		loadVote(vote);
	}
	
	/**
	 * Getter for {@link #closingMode}
	 * @return the {@link #closingMode}
	 */
	public boolean isClosingMode() {
		return closingMode;
	}


	/**
	 * Setter for {@link #closingMode}
	 * @param closingMode the {@link #closingMode} to set
	 */
	public void setClosingMode(boolean closingMode) {
		this.closingMode = closingMode;
	}

	public void build() {
		this.removeAll();
		Component left = left();
		Component center = center();
		if (left != null) this.add(left);
		if (center != null) this.add(center);
		
		if (option.getParent().isClosed() || closingMode) {
			winnerToggleButton = new OptionSelectToggleButton(this);
			winnerToggleButton.setEnabled(!option.getParent().isClosed() &&
					(currentUser != null && currentUser.equals(option.getParent().getOwner())));
			this.add(winnerToggleButton);
		} else {
			answerToggleButton = new AnswerListToggleButton(this, findAnswer());
			answerToggleButton.setEnabled(!option.getParent().isClosed() && vote.canEdit(currentUser));
			this.add(answerToggleButton);
		}
	}
	
	public void loadVote(Vote<?,?> vote) {
		this.vote = vote;
	}
	
	protected Answer<?,?> findAnswer() {
		vote.fillInMissingAnswers();
		for(Answer<?,?> answer : vote.getAnswers()) {
			if (answer.getOption().equals(option)) return answer;
		}
		return null;
	}
	
	protected Component left() {
		return null;
	}
	
	protected Component center() {
		VerticalLayout center = new VerticalLayout(label(), voteSummary());
		center.setSpacing(false);
		center.setPadding(false);
//		center.getStyle().set("border", "2px dotted DarkOrange"); //for debug purposes
		return center;
	}
	
	protected Component label() {
		Html label = new Html("<span>"+labelText()+"</span>");
		return label;
	}
	
	protected String labelText() {
		return option.getTitle();
	}
	
	protected Component voteSummary() {
		int countYes = 0, countNeedBe = 0, countNo = 0;
		for(Answer<?, ?> answer : option.getAnswers()) {
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
