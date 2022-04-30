package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.html.Span;

public class LineAwesomeIcon extends Span{

	public LineAwesomeIcon(String laName) {
		this.addClassNames("las", laName);
	}
	
}
