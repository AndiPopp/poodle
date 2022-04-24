package de.andipopp.poodle.views.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A simple standard confirmation dialog with an OK and a Cancel button.
 * Both buttons will close the dialog, but other listeners can be attached to both buttons.
 * @author Andi Popp
 *
 */
public class ConfirmationDialog extends Dialog {

	/**
	 * The minimum width for the button panel to avoid accidental miss-clicks
	 */
	private static final String MIN_BUTTONS_WIDTH = "240px";
	
	/**
	 * The OK button
	 */
	private Button ok = new Button("OK");
	
	/**
	 * The Cancel button
	 */
	private Button cancel = new Button("Cancel");
	
	/**
	 * The Yes button
	 */
	private Button yes = new Button("Yes");
	
	/**
	 * The No button
	 */
	private Button no = new Button("No");
	
	/**
	 * Message displaying the problem
	 */
	private Label message;
	
	/**
	 * Question asked to the user on how to proceed
	 */
	private Label question;

	/**
	 * @param message value for {@link #message}
	 * @param question value for {@link #question}
	 * @param YesNoInsteadOfOk if true, a {@link #yes} and {@link #no} will be used instead of {@link #ok}
	 */
	public ConfirmationDialog(String message, String question, boolean YesNoInsteadOfOk) {
		this.message = new Label(message);
		this.question = new Label(question);
		
		HorizontalLayout buttons = new HorizontalLayout(cancel);
		if (YesNoInsteadOfOk) buttons.add(no, yes);
		else buttons.add(ok);
		
		buttons.setMinWidth(MIN_BUTTONS_WIDTH);
		buttons.setJustifyContentMode(JustifyContentMode.BETWEEN);
		
		VerticalLayout dialogLayout = new VerticalLayout(this.message, this.question, buttons);
		dialogLayout.setPadding(false);
		
		ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
		
		ok.addClickListener(e -> this.close());
		cancel.addClickListener(e -> this.close());
		yes.addClickListener(e -> this.close());
		no.addClickListener(e -> this.close());
		
		this.add(dialogLayout);
	}
	
	public ConfirmationDialog(String message, String question) {
		this(message, question, false);
	}
	
	/**
	 * Add a listener to {@link #ok}
	 * @param listener the listener
	 */
	public void addOkListener(ComponentEventListener<ClickEvent<Button>> listener) {
		ok.addClickListener(listener);
	}
	
	/**
	 * Add a listener to {@link #cancel}
	 * @param listener the listener
	 */
	public void addCancelListener(ComponentEventListener<ClickEvent<Button>> listener) {
		ok.addClickListener(listener);
	}
	
	/**
	 * Add a listener to {@link #cancel}
	 * @param listener the listener
	 */
	public void addYesListener(ComponentEventListener<ClickEvent<Button>> listener) {
		yes.addClickListener(listener);
	}
	
	/**
	 * Add a listener to {@link #cancel}
	 * @param listener the listener
	 */
	public void addNoListener(ComponentEventListener<ClickEvent<Button>> listener) {
		no.addClickListener(listener);
	}
	
	
	
	
}
