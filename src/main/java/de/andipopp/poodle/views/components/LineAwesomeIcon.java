package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.html.Span;

public class LineAwesomeIcon extends Span{

	public LineAwesomeIcon(String laName) {
		if (!laName.startsWith("la-")) laName = "la-" + laName;
		this.addClassNames("las", laName);
	}
	
	public LineAwesomeIcon(String laName, String fontSize) {
		this(laName);
		this.getStyle().set("font-size", fontSize);
	}
	
}
