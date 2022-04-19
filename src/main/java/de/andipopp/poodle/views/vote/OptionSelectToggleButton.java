package de.andipopp.poodle.views.vote;

import com.vaadin.flow.component.html.Image;

public class OptionSelectToggleButton extends Image {

	OptionListItem parent;
	
	boolean winner;
	
	public OptionSelectToggleButton(OptionListItem parent) {
		this.parent = parent;
		winner = parent.getOption().isWinner();
		setCursor();
		this.setMinHeight("3ex");
		this.setMaxHeight("5ex");
		this.addClickListener(e -> toggleSelected());
		loadImage();
	}

	private void loadImage() {
		if (winner) {
			this.setSrc("images/VoteIcons-Winner.drawio.png");
			this.setAlt("selected");
		} else if (isEnabled()) {
			this.setSrc("images/VoteIcons-Candidate.drawio.png");
			this.setAlt("not selected");
		} else {
			this.setSrc("");
			this.setAlt("");
		}
		
	}

	private void toggleSelected() {
		winner = !winner;
		loadImage();
	}

	public void readToOption() {
		parent.getOption().setWinner(winner);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		loadImage();
		setCursor();
	}
	
	private void setCursor() {
		if (isEnabled()) this.addClassName("toggle");
		else this.removeClassName("toggle");
	}
	
	
}
