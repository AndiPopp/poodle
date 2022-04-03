package de.andipopp.poodle.views;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
        add(new MainLayout.MenuItemInfo.LineAwesomeIcon("la la-edit"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
	
//	/**
//     * Simple wrapper to create icons using LineAwesome iconset. See
//     * https://icons8.com/line-awesome
//     */
//    @NpmPackage(value = "line-awesome", version = "1.3.0")
//    public class LineAwesomeIcon extends Span {
//        public LineAwesomeIcon(String lineawesomeClassnames) {
//            addClassNames("menu-item-icon");
//            if (!lineawesomeClassnames.isEmpty()) {
//                addClassNames(lineawesomeClassnames);
//            }
//        }
//    }

}
