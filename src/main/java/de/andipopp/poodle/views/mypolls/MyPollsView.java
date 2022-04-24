package de.andipopp.poodle.views.mypolls;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.andipopp.poodle.views.MainLayout;

@PageTitle("My Polls")
@Route(value = "mypolls", layout = MainLayout.class)
@PermitAll
public class MyPollsView extends VerticalLayout {

}
