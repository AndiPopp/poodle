package de.andipopp.poodle.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.views.mypolls.MyPollsView;

@PageTitle("Welcome to Poodle")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class WelcomeView extends VerticalLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;

	public WelcomeView() {
		
        setSpacing(false);

//        Image img = new Image("images/empty-plant.png", "placeholder plant");
        Image img = new Image();
        img.setAlt("Placeholder Plant");
        img.setSrc("images/empty-plant.png");
        img.setWidth("200px");
        add(img);

        add(new H1("Welcome"));
        add(new H2("This place intentionally left empty"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
//        add(listHttpHeaders());
        
        WrappedSession session = VaadinRequest.getCurrent().getWrappedSession();
        add(new Paragraph(session.getId()));
        
        StreamResource res = new StreamResource("Test.ics", () -> testIcsAsInputStream());
        res.setContentType("text/calendar");
        
        Anchor anchor = new Anchor(res, "Click me to download");
        add(anchor);
       
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
	
	
	private InputStream testIcsAsInputStream() {
		try {
			return IOUtils.toInputStream(testIcs, "UTF-8");
		} catch (IOException e) {
			return null;
		}
	}
	
	private String testIcs = "BEGIN:VCALENDAR\n"
			+ "VERSION:2.0\n"
			+ "PRODID:-//ABC Corporation//NONSGML My Product//EN\n"
			+ "BEGIN:VEVENT\n"
			+ "SUMMARY:Lunchtime meeting\n"
			+ "UID:ff808181-1fd7389e-011f-d7389ef9-00000003\n"
			+ "DTSTART;TZID=America/New_York:20160420T120000\n"
			+ "DURATION:PT1H\n"
			+ "LOCATION:Mo's bar - back room\n"
			+ "END:VEVENT\n"
			+ "END:VCALENDAR";

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (VaadinRequest.getCurrent().getUserPrincipal().getName() != null) {
			UI.getCurrent().navigate(MyPollsView.class);
			event.rerouteTo(MyPollsView.class);
			
		}
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
