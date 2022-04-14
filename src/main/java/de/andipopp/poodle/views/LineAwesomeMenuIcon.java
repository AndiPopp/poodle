package de.andipopp.poodle.views;

import com.vaadin.flow.component.html.Span;

public class LineAwesomeMenuIcon extends Span{


	private static final long serialVersionUID = 1L;

	public LineAwesomeMenuIcon(String laName) {
		this.addClassNames("las", laName, "menu-pretext-icon");
	}
}
