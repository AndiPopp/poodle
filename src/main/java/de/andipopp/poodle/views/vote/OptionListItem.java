package de.andipopp.poodle.views.vote;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.Answer;
import de.andipopp.poodle.data.entity.polls.AnswerType;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.util.InvalidException;

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
			answerToggleButton = new AnswerListToggleButton(findAnswer());
			answerToggleButton.setEnabled(!option.getParent().isClosed() && vote.canEdit(currentUser));
			this.add(answerToggleButton);
		}
	}
	
	public void loadVote(Vote<?,?> vote) {
		this.vote = vote;
	}
	
	public void validateAnswerFromButton() throws InvalidException {
		answerToggleButton.validateAnswer();
	}
	
	public void readAnswerFromButton() {
		answerToggleButton.readValueToAnswer();
	}
	
	public void readWinnerFromButton() {
		winnerToggleButton.readToOption();
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
		//count answers
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

		
		//build component
		Span result = new Span();
		result.addClassName("vote-count-inline");
		Span yes = new Span(""+countYes+" ");
		yes.addClassName("vote-yes");
		Image imgYes = new Image("images/VoteIcons-Yes.drawio.png", "in favor");
		imgYes.addClassName("vote-count-inline-vote-image");
		result.add(yes, imgYes);
		
		if (countNeedBe > 0) {
			Span ifNeedBe = new Span(" / "+countNeedBe+" ");
			ifNeedBe.addClassName("vote-if-need-be");
			Image imgNeedBe = new Image("images/VoteIcons-IfNeedBe.drawio.png", "in favor if need be");
			imgNeedBe.addClassName("vote-count-inline-vote-image");
			result.add(ifNeedBe, imgNeedBe);
		}
		Span no = new Span(" / "+countNo+" ");
		no.addClassName("vote-no");
		Image imgNo = new Image("images/VoteIcons-No.drawio.png", "opposed");
		imgNo.addClassName("vote-count-inline-vote-image");
		result.add(no, imgNo);
		
		//add click listener
		result.addClickListener(e -> optionVoteList().open());
		result.getStyle().set("cursor", "pointer");
		
		//return result
		return result;
	}
	
	protected String optionSummary() {
		return option.getTitle();
	}
	
	private Dialog optionVoteList() {
		//build the title
		Label title = new Label(optionSummary());
		title.getStyle().set("font-weight", "bold");
		
		//build the yes vote list
		AnswerBlock yesVoteBlock = new AnswerBlock(AnswerType.YES, "/images/VoteIcons-Yes.drawio.png", "Yes");
		AnswerBlock ifNeedBeVoteBlock = new AnswerBlock(AnswerType.IF_NEED_BE, "/images/VoteIcons-IfNeedBe.drawio.png", "If need be");
		AnswerBlock noVoteBlock = new AnswerBlock(AnswerType.NO, "/images/VoteIcons-No.drawio.png", "No");
		
		VerticalLayout dialogLayout = new VerticalLayout(title);
		if (yesVoteBlock.count > 0) dialogLayout.add(yesVoteBlock);
		if (ifNeedBeVoteBlock.count > 0) dialogLayout.add(ifNeedBeVoteBlock);
		if (noVoteBlock.count > 0) dialogLayout.add(noVoteBlock);
		
		dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.START);
		dialogLayout.setPadding(false);
		Dialog dialog = new Dialog(dialogLayout);
		
		Button closeButton = new Button("Close");
		closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		closeButton.addClickListener(e -> dialog.close());
		HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
		buttonLayout.setWidthFull();
		buttonLayout.setJustifyContentMode(JustifyContentMode.END);
		dialogLayout.add(buttonLayout);
		dialog.setModal(true);
		
		return dialog;
	}
	
	private class AnswerBlock extends HorizontalLayout{
		private static final long serialVersionUID = 1L;
		
		int count;
		
		AnswerBlock(AnswerType answerType, String imageSrc, String imageAlt) {
			Image imgVote = new Image(imageSrc, imageAlt);
			imgVote.setHeight("3ex");
			this.add(imgVote);
			this.setDefaultVerticalComponentAlignment(Alignment.START);
			VerticalLayout voteList = new VerticalLayout();
			voteList.setPadding(false);
			count = 0;
			for(Answer<?,?> answer : getOption().sortAnswersByDisplayName()) { //TODO use the sorted version once we properly detach the new vote
				if(answer.getValue() == answerType) {
					count++;
					Avatar avatar = answer.getVote().getAvatar();
					avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
					HorizontalLayout entry = new HorizontalLayout(avatar, new Label(answer.getVote().getDisplayName()));
					voteList.add(entry);
				}
			}
			this.add(voteList);
		}
	}
	
	
}
