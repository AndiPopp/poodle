package de.andipopp.poodle.views.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.views.MainLayout;

@PageTitle("View Poll")
@Route(value = "test", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class TestView extends VerticalLayout {

	/**
	 * @param pollService
	 */
	public TestView() {

		this.add(testBox("320px"));
		this.add(testBox("480px"));
		this.add(testBox("640px"));
		this.add(testBox("800px"));
		this.add(testBox("1080px"));
		
        WrappedSession session = VaadinRequest.getCurrent().getWrappedSession();
        add(new Paragraph(session.getId()));
        
        StreamResource res = new StreamResource("Test.ics", () -> testIcsAsInputStream());
        res.setContentType("text/calendar");
        
        Anchor anchor = new Anchor(res, "Click me to download");
        add(anchor);
	}
	
	private HorizontalLayout testBox(String width) {
		HorizontalLayout horizontalLayout = new HorizontalLayout(new Label(width));
		horizontalLayout.setWidth(width);
		horizontalLayout.getStyle().set("border", "2px dotted FireBrick");
		return horizontalLayout;
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


}
