package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.html.Span;

public class LineAwesomeMenuIcon extends Span{

	public LineAwesomeMenuIcon(String laName) {
		this.addClassNames("las", laName, "menu-pretext-icon");
	}
}
