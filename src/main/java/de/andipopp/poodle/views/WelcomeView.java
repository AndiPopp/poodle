package de.andipopp.poodle.views;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Welcome to Poodle")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

	public WelcomeView() {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        add(new H1("Welcome"));
        add(new H2("This place intentionally left empty"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
//        add(listHttpHeaders());
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
	
//	private Component listHttpHeaders() {
//		VerticalLayout result = new VerticalLayout();
//		for(Iterator<String> it = VaadinRequest.getCurrent().getHeaderNames().asIterator(); it.hasNext();) {
//			String name = it.next();
//			result.add(new Paragraph(name + ": " + VaadinRequest.getCurrent().getHeader(name)));
//		}
//		Locale locale = VaadinRequest.getCurrent().getLocale();
//		result.add(new Paragraph("Locale: "+getLocale()));
//		result.add(new Paragraph(new GregorianCalendar(locale).getTimeZone().getID()));
//		return result;
//	}

}
