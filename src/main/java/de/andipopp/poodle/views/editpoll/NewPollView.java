package de.andipopp.poodle.views.editpoll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.views.MainLayout;

@PageTitle("New Poodle Poll")
@Route(value = "new", layout = MainLayout.class)
@PermitAll
public class NewPollView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;

	private static final String MAX_WIDTH = "800px";
	
	private PollSelectorBox simplePollSelector = new PollSelectorBox(
			"la-vote-yea",
			"Simple Poll",
			"Name a few options and get people to vote for them. The best kind of poll for when you "
			+ "do not need the fancy features."
			
		);
	
	private PollSelectorBox datePollSelector = new PollSelectorBox(
		"la-calendar",
		"Date Poll",
		"Planning a barbeque, board game session or some other kind of event? Use a date poll "
		+ "to offer date and time options, compare scheduling conflicts and export the results as ics file."
	);
	
	public NewPollView() {
		VerticalLayout content = new VerticalLayout(simplePollSelector, datePollSelector);
		content.setMaxWidth(MAX_WIDTH);
		add(content);
		this.setJustifyContentMode(JustifyContentMode.CENTER);
		
		datePollSelector.addClickListener(e -> UI.getCurrent().navigate("edit", newPollQueryParameters(DatePoll.TYPE_NAME)));
	}


	private QueryParameters newPollQueryParameters(String pollTypeName) {
		Map<String, List<String>> map = new HashMap<>(1);
		List<String> list = new ArrayList<>(1);
		list.add(pollTypeName);
		map.put(EditPollView.CREATE_POLL_KEY, list);
		return new QueryParameters(map);
	}


	/**
	 * A layout which basically functions as a large button.
	 * Has a title and a paragraph with a description
	 * @author Andi Popp
	 *
	 */
	private static class PollSelectorBox extends VerticalLayout{

		private static final long serialVersionUID = 1L;
		
		PollSelectorBox(String iconName, String typeName, String description) {
			this.addClassName("text-box-button");
			
			Span icon = new Span();
			icon.getStyle().set("font-size", "48pt");
			icon.addClassName("las");
			icon.addClassName(iconName);
			icon.getStyle().set("vertical-align", "middle");
			
			
			Span title = new Span(typeName);
			title.addClassNames("text-box-button-text", "title");
			
			Paragraph desc = new Paragraph(description);
			desc.addClassNames("text-box-button-text", "description");
			
			Div text = new Div(title, desc);
			
			HorizontalLayout content = new HorizontalLayout(icon, text);
			content.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			this.add(content);
		}
	}
	
}
