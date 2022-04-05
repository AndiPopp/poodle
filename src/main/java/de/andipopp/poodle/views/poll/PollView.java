package de.andipopp.poodle.views.poll;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.jsoup.Jsoup;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.util.JSoupUtils;
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

	private AbstractPoll<?,?> poll;
	
	private VerticalLayout content;
	
	/* =====================
	 * = Layout Components =
	 * ===================== */
	
	private HorizontalLayout header = new HorizontalLayout();
	
	private H6 subtitle = new H6();
	
	private VerticalLayout description = new VerticalLayout();

	
	/**
	 * @param pollService
	 */
	public PollView(PollService pollService) {
		super();
		this.pollService = pollService;
		this.setDefaultHorizontalComponentAlignment(Alignment.START);
		this.content = new VerticalLayout();
		this.content.setPadding(false);
		this.content.add(notFound());
	    this.add(content);
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
				Optional<AbstractPoll<?,?>> opt = pollService.get(pollId);
				if (!opt.isEmpty()) {
					loadPoll(opt.get());
				}
			} catch (NotAUuidException e) {
				//do nothing, keep the "not found"
			}
		}
	}
	
	private void loadPoll(AbstractPoll<?,?> poll) {
		//load the poll itself
		this.poll = poll;
		//strip content from all its components (especially the "not found")
		this.content.removeAll();
		//add the poll specific content
		this.content.add(metaInfBlock());
//		this.content.getStyle().set("border", "2px dotted DarkOrange") ; //for debug purposes
	}

	/* ===============
	 * = Sub-Layouts =
	 * =============== */
	
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
	
	private VerticalLayout metaInfBlock() {
		VerticalLayout metaInfBlock = new VerticalLayout();
		metaInfBlock.setPadding(false);
		metaInfBlock.add(configureHeader());
		metaInfBlock.add(configureSubtitle());
		metaInfBlock.add(configureDescription());
		return metaInfBlock;
	}
	
	private Component configureHeader() {
		header.removeAll();
		header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		Avatar ownerAvatar = poll.getOwner().getAvatar();
		ownerAvatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
//		ownerAvatar.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		this.content.add(ownerAvatar);
		H3 title = new H3(poll.getTitle());
		title.getStyle().set("display", "inline");
		title.getStyle().set("margin-top", "0ex");
		title.getStyle().set("margin-bottom", "0ex");
//		title.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		header.add(ownerAvatar, title);
//		header.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		return header;
	}
	
	private Component configureSubtitle() {
		String text = "created ";
		Duration duration = Duration.between(poll.getCreateDate(), Instant.now());
		text += duration.toDays() +" day(s) ago ";
		text += "by " + poll.getOwner().getName();
		text +=  " and retained until "+poll.getDeleteDate();
		subtitle.setText(text);
		subtitle.getStyle().set("margin-top", "0ex");
//		subtitle.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		return subtitle;
	}
	
	private Component configureDescription() {
		description.removeAll();
		if (poll.getDescription() != null && !poll.getDescription().isBlank()) description.add(new Html("<p>" + Jsoup.clean(poll.getDescription(), JSoupUtils.BASIC) + "</p>"));
		if (poll instanceof DatePoll) {
			DatePoll datePoll = (DatePoll) poll;
			if (datePoll.getLocation() != null && !datePoll.getLocation().isBlank()) 
				description.add(new HorizontalLayout(new Icon(VaadinIcon.MAP_MARKER), new Html("<span>"+Jsoup.clean(datePoll.getLocation(), JSoupUtils.BASIC)+"</span>")));
		}
		description.setSpacing(false);
		description.setPadding(false);
		return description;
	}
	
}