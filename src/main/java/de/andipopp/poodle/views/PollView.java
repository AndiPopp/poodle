package de.andipopp.poodle.views;

import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinRequest;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.util.NotAUuidException;
import de.andipopp.poodle.util.UUIDUtils;

public class PollView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	
	public static final String ID_PARAMETER_NAME = "pollId";
	
	protected AbstractPoll<?,?> poll;

	protected PollService pollService;
	
	protected User currentUser;
	
	
	public PollView(UserService userService, PollService pollService) {
		//remember the current user
		if (VaadinRequest.getCurrent().getUserPrincipal()!=null) {
			String userName = VaadinRequest.getCurrent().getUserPrincipal().getName(); 
			this.currentUser = userService.get(userName);
		}else {
			this.currentUser = null;
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

	protected void loadPoll(AbstractPoll<?,?> poll) {
		this.poll = poll;
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		if (queryParameters.getParameters().containsKey(ID_PARAMETER_NAME)) {
			try {
				String pollIdBase64url = queryParameters.getParameters().get(ID_PARAMETER_NAME).get(0);
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
}
