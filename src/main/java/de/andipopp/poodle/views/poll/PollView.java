package de.andipopp.poodle.views.poll;

import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.util.NotAUuidException;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.MainLayout;


@PageTitle("Poodle Poll")
@Route(value = "poll", layout = MainLayout.class)
@AnonymousAllowed
public class PollView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	
	public static final String ID_PARAMETER_NAME = "pollId";
	
	private PollService pollService;

	private AbstractPoll<?> poll;
	
	private VerticalLayout content;
	
	/**
	 * @param pollService
	 */
	public PollView(PollService pollService) {
		super();
		this.pollService = pollService;
		this.content = new VerticalLayout();
		this.content.add(notFound());
	    this.add(content);
	}
	
	private static VerticalLayout notFound() {
		VerticalLayout notFound = new VerticalLayout();
		notFound.setSpacing(false);
		
	    Image img = new Image("images/empty-plant.png", "placeholder plant");
	    img.setWidth("200px");
	    notFound.add(img);
	
	    notFound.add(new H2("Poll not found"));
	    notFound.add(new Paragraph("Sorry, the poll could not be found. Do you have the correct URL?"));
	    notFound.setSizeFull();
	    notFound.setJustifyContentMode(JustifyContentMode.CENTER);
	    notFound.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	    notFound.getStyle().set("text-align", "center");
	    return notFound;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		if (queryParameters.getParameters().containsKey(ID_PARAMETER_NAME)) {
			try {
				String pollIdBase64url = queryParameters.getParameters().get(ID_PARAMETER_NAME).get(0);
//				System.out.println("Trying to load poll " + pollIdBase64url + " / " + UUIDUtils.base64urlToUuid(pollIdBase64url));
				UUID pollId = UUIDUtils.base64urlToUuid(pollIdBase64url);
				Optional<AbstractPoll<?>> opt = pollService.get(pollId);
				if (!opt.isEmpty()) {
					loadPoll(opt.get());
				}else {
					System.out.println("Did not find anything :(");
				}
			} catch (NotAUuidException e) {
				//do nothing, keep the "not found"
			}
		}
	}
	
	private void loadPoll(AbstractPoll<?> poll) {
		this.poll = poll;
		this.content.removeAll();
		this.content.add("Loading poll "+UUIDUtils.uuidToBase64url(poll.getId()));
	}

}