package de.andipopp.poodle.views;

import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.views.editpoll.NewPollView;
import de.andipopp.poodle.views.mypolls.MyPollsView;
import de.andipopp.poodle.views.test.TestView;
import de.andipopp.poodle.views.usersettings.UserSettingsView;
import de.andipopp.poodle.views.viewpolls.ViewPollsView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

	/**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

		private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeMenuIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeMenuIcon extends Span {

			public LineAwesomeMenuIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        setPrimarySection(Section.DRAWER);
        addToNavbar(createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle", "vanish-above-800px");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        
        viewTitle = new H1();
        viewTitle.addClassNames("view-title");
        HorizontalLayout titleWrapper = new HorizontalLayout(viewTitle);
        titleWrapper.setWidthFull();
        titleWrapper.addClassName("title-wrapper");
        Header header = new Header(toggle, titleWrapper);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Poodle");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("New Poll", "la la-plus-circle", NewPollView.class), //

                new MenuItemInfo("View Polls", "la la-poll-h", ViewPollsView.class), //

                new MenuItemInfo("My Polls", "la la-poll", MyPollsView.class), //

                new MenuItemInfo("User Settings", "la la-user-edit", UserSettingsView.class), //

                new MenuItemInfo("Test View", "la la-vial", TestView.class), //
        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> {
                authenticatedUser.logout();
            });

            Span name = new Span(user.getName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
    	if (getContent() instanceof HasDynamicTitle) {
    		return ((HasDynamicTitle) getContent()).getPageTitle();
    	} else {
	        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class); 
	        return title == null ? "" : title.value();
    	}
    }
}
