package de.andipopp.poodle.views.adminpanel;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.andipopp.poodle.data.Role.UserRoleCheckboxGroup;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.service.UserService;

@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {
	
	private static final String MAX_CONTENT_WIDTH = "700px";
	
	UserService userService;
	
	Grid<User> grid = new Grid<>(User.class);
	
	TextField filterUserName = new TextField();
	
	TextField filterDisplayName = new TextField();
	
	VerticalLayout content = new VerticalLayout();
	
	public UsersView(UserService userService) {
		this.userService = userService;
		
		filterUserName.setPlaceholder("Filter by user name...");
		filterUserName.setValueChangeMode(ValueChangeMode.LAZY);
		filterUserName.addValueChangeListener(e -> updateGrid());
		filterDisplayName.setPlaceholder("Filter by display name...");
		filterDisplayName.setValueChangeMode(ValueChangeMode.LAZY);
		filterDisplayName.addValueChangeListener(e -> updateGrid());
		
		configureGrid();
		
		content.add(grid);
		content.setPadding(false);
		content.setHeightFull();
		content.setMaxWidth(MAX_CONTENT_WIDTH);
		this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		this.setHeightFull();;
		this.add(content);
	}

	private void configureGrid() {
		grid.removeAllColumns(); //empty out
		grid.addColumn(new ComponentRenderer<>(user -> new GotoUserAnchor(user)))
			.setHeader("User Name")
			.setComparator(User::getUsername)
//			.setWidth("10em")
//			.setFlexGrow(1)
		;
		grid.addColumn("displayName")
//			.setWidth("15em")
//			.setFlexGrow(2)
		;
		grid.addColumn(new ComponentRenderer<>(user -> new UserRoleCheckboxGroup(user)))
			.setHeader("Roles")
		;
		
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
		
		updateGrid();
	}
	
	private void updateGrid() {
		grid.setItems(userService.filter(filterUserName.getValue().strip(), filterDisplayName.getValue().strip()));
	}
}
