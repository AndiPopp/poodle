package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.html.Label;

import de.andipopp.poodle.data.util.HasUuid;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.util.UUIDUtils.Part;

public class DebugLabel extends Label{

	public DebugLabel() {
		this.addClassName("vanish-below-480px");
		configureStyle();
	}

	public DebugLabel(HasUuid object) {
		this();
		setText(object);
	}
	
	private void configureStyle() {
		getElement().getThemeList().add("badge contrast small");
		getStyle().set("color", "var(--lumo-contrast-40pct)");
	}
	
	public void setText(HasUuid object) {
		if (object != null && object.getId() != null) setText("id2: "+UUIDUtils.getPart(object.getId(), Part.PART2));
		else if(object != null) setText("new");
		else setText("null");
		
	}
	
}
