package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ErrorMessage extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	public ErrorMessage(String title, String message) {
		super();
		setSpacing(false);
		
	    Image img = new Image("images/empty-plant.png", "placeholder plant");
	    img.setWidth("200px");
	    add(img);
	
	    add(new H2(title));
	    add(new Paragraph(message));
	    setSizeFull();
	    setJustifyContentMode(JustifyContentMode.CENTER);
	    setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	    getStyle().set("text-align", "center");
	}
}
