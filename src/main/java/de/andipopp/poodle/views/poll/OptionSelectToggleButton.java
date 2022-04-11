package de.andipopp.poodle.views.poll;

import com.vaadin.flow.component.html.Image;

public class OptionSelectToggleButton extends Image {

	private static final long serialVersionUID = 1L;

	OptionListItem parent;
	
	public OptionSelectToggleButton(OptionListItem parent) {
		this.parent = parent;
		setCursor();
		this.setMinHeight("3ex");
		this.setMaxHeight("5ex");
		this.addClickListener(e -> toggleSelected());
		loadImage();
	}

	private void loadImage() {
		if (parent.getOption().isWinner()) {
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
		parent.getOption().setWinner(!parent.getOption().isWinner());
		loadImage();
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
