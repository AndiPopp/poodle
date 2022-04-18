package de.andipopp.poodle.views.vote;

import java.time.Duration;
import java.time.Instant;

import org.jsoup.Jsoup;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.HtmlUtils;
import de.andipopp.poodle.util.JSoupUtils;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.LineAwesomeMenuIcon;
import de.andipopp.poodle.views.MainLayout;
import de.andipopp.poodle.views.PollView;
import de.andipopp.poodle.views.vote.date.DatePollListView;


@PageTitle("Poodle Poll")
@Route(value = "poll", layout = MainLayout.class)
@AnonymousAllowed
/**
 * The core view to vote in a poll.
 * @author Andi Popp
 *
 */
public class VoteView extends PollView {
		
	private static final long serialVersionUID = 1L;

	private VoteService voteService;

	/* =====================
	 * = Layout Components =
	 * ===================== */
	

	private VerticalLayout content;
	
	private VerticalLayout pollContent;
	
	private HorizontalLayout header = new HorizontalLayout();
	
	private H6 subtitle = new H6();
	
	private VerticalLayout info = new VerticalLayout();

	
	ViewToggleState state = ViewToggleState.LIST;
	
	private enum ViewToggleState {
		LIST, TABLE
	}
	
	PollListView<?, ?> listView;
	
	public VoteView(AuthenticatedUser authenticatedUser, PollService pollService, VoteService voteService) {
		super(authenticatedUser, pollService);
		//hookup the vote service
		this.voteService = voteService;
		
		//set the toggle state to default
		state = ViewToggleState.LIST; //TODO decide default on Mobile or Desktop Browser
		
		this.setDefaultHorizontalComponentAlignment(Alignment.START);
		
		//we use content as an intermediary, so we remove padding from this
		this.setPadding(false);
		//add a not found as default content
		this.content = new VerticalLayout();
		this.content.add(notFound());
	    this.add(content);
//	    this.content.getStyle().set("border", "2px dotted FireBrick"); //for debug purposes
	}

	@Override
	protected void loadPoll(AbstractPoll<?,?> poll) {
		//load the poll itself
		super.loadPoll(poll);

		//add the poll specific content
		this.pollContent = new VerticalLayout();
		this.pollContent.setPadding(false);
		this.pollContent.add(metaInfBlock());
		this.pollContent.setSpacing(false);
//		this.pollContent.getStyle().set("border", "2px dotted FireBrick"); //for debug purposes
		
		if (poll instanceof DatePoll) {
			listView = new DatePollListView((DatePoll) poll, getCurrentUser(), voteService, pollService); 
			listView.addListener(PollListView.ViewChangeEvent.class, e -> configureTopRightContextMenu());
			//TODO also build table view
			if (state == ViewToggleState.LIST) this.pollContent.add(listView);
		}
		
		//now that we know the view toggle state, we can build the top right context menu
		configureTopRightContextMenu();
		
		//strip content from all its components (especially the "not found")
		this.content.removeAll();
		HorizontalLayout pollContentWrapper = new HorizontalLayout(pollContent);
		pollContentWrapper.setPadding(false);
		pollContentWrapper.setMinWidth("50%");
		
		VerticalLayout horizontalAlignmentWrapper = new VerticalLayout();
		horizontalAlignmentWrapper.setSizeFull();
		horizontalAlignmentWrapper.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		horizontalAlignmentWrapper.add(pollContentWrapper);
		horizontalAlignmentWrapper.setPadding(false);
		this.content.add(horizontalAlignmentWrapper);
	}

	
	/* ===============
	 * = Sub-Layouts =
	 * =============== */
	
	private Component metaInfBlock() {
		VerticalLayout metaInfBlock = new VerticalLayout();
		metaInfBlock.setPadding(false);
		metaInfBlock.setDefaultHorizontalComponentAlignment(Alignment.START);
		metaInfBlock.add(configureHeader());
		metaInfBlock.add(configureSubtitle());
		metaInfBlock.add(configureInfo());
		metaInfBlock.add(HtmlUtils.pollShareScript(getPoll()));
//		metaInfBlock.getStyle().set("border", "2px dotted Red"); //for debug purposes
		return metaInfBlock; //new HorizontalLayout(metaInfBlock);
	}
	
	private Component configureHeader() {
		header.removeAll();
		header.setWidthFull();
		header.setDefaultVerticalComponentAlignment(Alignment.START);
		Avatar ownerAvatar = poll.getOwner().getAvatarCopy();
		ownerAvatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
		ownerAvatar.getStyle().set("border", "3px solid black") ;
		this.content.add(ownerAvatar);
		H3 title = new H3(poll.getTitle());
		title.getStyle().set("display", "inline");
		title.getStyle().set("margin-top", "0ex");
		title.getStyle().set("margin-bottom", "0ex");
//		title.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		title.setWidthFull();
		HorizontalLayout avatarAndTitleWrapper = new HorizontalLayout(ownerAvatar, title);
		avatarAndTitleWrapper.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		avatarAndTitleWrapper.setWidthFull();
//		avatarAndTitleWrapper.getStyle().set("border", "2px dotted DarkOrange") ; //for debug purpose
		header.add(avatarAndTitleWrapper);
//		header.getStyle().set("border", "2px dotted Green") ; //for debug purposes

		
		//build the menu button:	
		topRightMenu = new Button();
		topRightMenu.addClassName("primary-text");
		topRightMenu.setIcon(new Icon(VaadinIcon.MENU));
		topRightMenu.addThemeVariants(ButtonVariant.LUMO_ICON);
		
		
		HorizontalLayout headerMenuButtonWrapper = new HorizontalLayout(topRightMenu);
		headerMenuButtonWrapper.setHeightFull();
		headerMenuButtonWrapper.setDefaultVerticalComponentAlignment(Alignment.START);
//		headerMenuButtonWrapper.getStyle().set("border", "2px dotted FireBrick") ; //for debug purposes
		header.add(headerMenuButtonWrapper);
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
//		subtitle.getStyle()
		return subtitle;
	}
	
	private Component configureInfo() {
		info.removeAll();
		if (poll.getDescription() != null && !poll.getDescription().isBlank()) {
			HorizontalLayout description = new HorizontalInfoContainer(true);
			description.add(new Html("<p>" + Jsoup.clean(poll.getDescription(), JSoupUtils.BASIC) + "</p>"));
			description.setJustifyContentMode(JustifyContentMode.BETWEEN);
			info.add(description);
		}
		if (poll instanceof DatePoll) {
			DatePoll datePoll = (DatePoll) poll;
			if (datePoll.getLocation() != null && !datePoll.getLocation().isBlank()) {
				HorizontalLayout location = new HorizontalInfoContainer(true);
				location.add(new HorizontalInfoContainer(false, new Icon(VaadinIcon.MAP_MARKER), new Html("<span>"+Jsoup.clean(datePoll.getLocation(), JSoupUtils.BASIC)+"</span>")));
				location.setJustifyContentMode(JustifyContentMode.BETWEEN);
				info.add(location);
			}	
		};
		info.setSpacing(false);
		info.setPadding(false);
//		info.getStyle().set("border", "2px dotted Green") ; //for debug purposes
		return info;
	}
	

	/* ==================================
	 * = Members to configure poll menu =
	 * ================================== */
	
	/**
	 * The button to open the poll menu
	 */
	private Button topRightMenu;
	
	/**
	 * The actual menu
	 */
	private ContextMenu topRightContextMenu;
	
	/**
	 * Remember the item to switch to table view to toggle visibility
	 */
	private MenuItem switchToTableView;
	
	/**
	 * Remember the item to switch to list view to toggle visibility
	 */
	private MenuItem switchToListView;
	
	/**
	 * Remember the closing item to switch labels
	 */
	private MenuItem close;
	
	private void configureTopRightContextMenu() {
		if (topRightContextMenu != null) topRightContextMenu.setTarget(null);
		topRightContextMenu = new ContextMenu();
		topRightContextMenu.removeAll();
		
		topRightContextMenu.addItem(new Html("<span onclick=\""
				+ "share_" + UUIDUtils.uuidToBase64url(poll.getId()) +  "()" 
				+ "\"><span class=\"las la-share menu-pretext-icon\"></span> Share"
				+ "</span>"));
		
		if (currentUser != null && currentUser.equals(poll.getOwner())) {
			MenuItem edit = topRightContextMenu.addItem(" Edit", e -> 
				UI.getCurrent().navigate("edit", getPoll().buildQueryParameters(true))
			);
			edit.addComponentAsFirst(new LineAwesomeMenuIcon("la-edit"));
			
			if (poll.isClosed()) {
				MenuItem reopen = topRightContextMenu.addItem(" Reopen Poll", e -> {
					poll.setClosed(false);
					listView.buildAll();
					configureTopRightContextMenu();
				});
				reopen.addComponentAsFirst(new LineAwesomeMenuIcon("la-lock-open"));
			} else {		
				String closeLabelOpen = " Select Winners";
				String closeLabelClosing = " Exit Winner Select";
				close = topRightContextMenu.addItem(closeLabelOpen, e->{
					listView.toggleClosingMode();
					if (listView.isClosingMode()) close.setText(closeLabelClosing);
					else close.setText(closeLabelOpen);
					close.addComponentAsFirst(new LineAwesomeMenuIcon("la-award"));
				});
				close.addComponentAsFirst(new LineAwesomeMenuIcon("la-award"));
			}
		}
		
		switchToTableView = topRightContextMenu.addItem(" Table View", e -> {});
		switchToTableView.addComponentAsFirst(new LineAwesomeMenuIcon("la-table"));
		switchToTableView.setVisible(state == ViewToggleState.LIST);
		
		switchToListView = topRightContextMenu.addItem(" List View", e -> {});
		switchToListView.addComponentAsFirst(new LineAwesomeMenuIcon("la-list-ul"));
		switchToListView.setVisible(state == ViewToggleState.TABLE);
		
		if (poll instanceof DatePoll) {
			MenuItem conflictSettings = topRightContextMenu.addItem(" Date Conflicts", e -> {});
			conflictSettings.addComponentAsFirst(new LineAwesomeMenuIcon("la-calendar-times"));
		}
		
		topRightContextMenu.setOpenOnClick(true);
		topRightContextMenu.setTarget(topRightMenu);
	}
	
	private static class HorizontalInfoContainer extends HorizontalLayout {

		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		public HorizontalInfoContainer(boolean fullWidth) {
			super();
			setAttributes(fullWidth);
		}
		
		/**
		 * @param children
		 */
		public HorizontalInfoContainer(boolean fullWidth, Component... children) {
			super(children);
			setAttributes(fullWidth);
		}

		private void setAttributes(boolean fullWidth) {
			this.setPadding(false);
			if (fullWidth) this.setWidthFull();
			this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
//			this.getStyle().set("border", "2px dotted DarkOrange") ; //for debug purposes
		}
		
	}  
}