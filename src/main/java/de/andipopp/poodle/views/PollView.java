package de.andipopp.poodle.views;

import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.NotAUuidException;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.editpoll.ErrorMessage;

public class PollView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	
	protected AbstractPoll<?,?> poll;

	private String editKey;
	
	protected PollService pollService;
	
	protected User currentUser;
	
	protected QueryParameters queryParameters;
	
	public PollView(AuthenticatedUser authenticatedUser, PollService pollService) {
		//remember the current user
		if (authenticatedUser.get().isEmpty()) {
			currentUser = null;
		}else {
			currentUser = authenticatedUser.get().get();
		}
		
    	//hook up the poll service 
		this.pollService = pollService;
	}
	
	/**
	 * Getter for {@link #poll}
	 * @return the {@link #poll}
	 */
	public AbstractPoll<?, ?> getPoll() {
		return poll;
	}

	/**
	 * Getter for {@link #pollService}
	 * @return the {@link #pollService}
	 */
	public PollService getPollService() {
		return pollService;
	}



	/**
	 * Getter for {@link #currentUser}
	 * @return the {@link #currentUser}
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Getter for {@link #editKey}
	 * @return the {@link #editKey}
	 */
	protected String getEditKey() {
		return editKey;
	}

	protected void loadPoll(AbstractPoll<?,?> poll) {
		this.poll = poll;
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		this.queryParameters = queryParameters;
		
		//hand the parameters over to possible sub-class parsing. If the result is true stop here.
		if (parseQueryParameters(queryParameters)) return;
		
		//load the edit key if present
		if (queryParameters.getParameters().containsKey(AbstractPoll.EDIT_KEY_PARAMETER_NAME)) {
			editKey = queryParameters.getParameters().get(AbstractPoll.EDIT_KEY_PARAMETER_NAME).get(0);
		}
		
		//load the poll if we have an ID
		if (queryParameters.getParameters().containsKey(AbstractPoll.ID_PARAMETER_NAME)) {
			try {
				String pollIdBase64url = queryParameters.getParameters().get(AbstractPoll.ID_PARAMETER_NAME).get(0);
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
	
	/**
	 * This method tries to make sense of the query parameters. It if did it will return true.
	 * This method can be overloaded by sub-classes to intervene before the default behavior
	 * of {@link #beforeEnter(BeforeEnterEvent)} by returning true. The default will return false. 
	 * @return true if the method parsed the parameters, false otherwise
	 */
	protected boolean parseQueryParameters(QueryParameters queryParameters) {
		return false;
	}
	
	protected static VerticalLayout notFound() {
		return new ErrorMessage("Poll not found", "Sorry, the poll could not be found. Do you have the correct URL?");
	}

}
