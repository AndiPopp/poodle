package de.andipopp.poodle.views;

import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.security.AuthenticatedUser;
import de.andipopp.poodle.util.NotAUuidException;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.editpoll.ErrorMessage;

/**
 * Basic super-class for views that manipulate a poll indicated by query parameters.
 * 
 * <p>This layout has no components of its own but provides key features for sub-classes which are meant
 * to manipulate some kind of {@link AbstractPoll}. The core is the field {@link #poll} itself which stores
 * the poll associated with this view.</p>
 * 
 * <p>Setting the field is meant to be done by finding the poll in the back-end for which the class contains
 * a {@link #pollService}. This service can then also be used to write any changes back to the back-end. To actually 
 * set the {@link #poll}, this class implements {@link BeforeEnterObserver}. In searches the query parameters for
 * a parameter named {@value AbstractPoll#ID_PARAMETER_NAME} and expects the value to be a base64url representation
 * of a polls UUID. If a poll with that ID is found, this class sets the field via {@link #loadPoll(AbstractPoll)}.</b>
 * 
 * <b>The {@link #beforeEnter(BeforeEnterEvent)} event also has a feature to pass the queries onto the sub-classes.
 * This is done via the method {@link #parseQueryParameters(QueryParameters)}. In its raw form, this method will just
 * return false, indicating that this class should handle the query parameters in {@link #beforeEnter(BeforeEnterEvent)}.
 * Sub-classes can override the method and in cases where the overriding methods return true, {@link BeforeEnterObserver}
 * will stop without interpreting the query parameters. Sub-class which are not meant to interfere with this class's
 * interpretation of the query parameters but still need access to them, can do so via the field {@link #queryParameters}.</b>
 * 
 * <b>The method {@link #loadPoll(AbstractPoll)} provides basic setter features for {@link #poll}, but is also meant
 * to be expanded by sub-classes to trigger the building of their poll specific elements.</b>
 * 
 * <b>Some features in poll views are restricted to specific users, e.g. an admin or the poll's owner. This class
 * therefore also remembers the {@link #currentUser}. Since this type of view is not meant to manipulate the 
 * {@link AuthenticatedUser} itself, it only remembers the wrapped {@link User} object.</b>
 * 
 * <b>As a final convenience feature this class also provides a static {@link #notFound()} to construct a view
 * indicating that no poll with the given id has been found.</b>
 * 
 * @author Andi Popp
 *
 */
public class PollView extends VerticalLayout implements BeforeEnterObserver {

	/**
	 * Poll associated with this view
	 */
	protected AbstractPoll<?,?> poll;

	/**
	 * Service used to load the poll and store changes from and to the back-end
	 */
	protected PollService pollService;
	
	/**
	 * The current user of this view, can be <code>null</code> in case of an anonymous user
	 */
	protected User currentUser;
	
	/**
	 * If the query parameters contained an edit-key, we store it here for convenience purposes
	 */
	private String editKey;
	
	/**
	 * The query parameters loaded from {@link #beforeEnter(BeforeEnterEvent)} in case a sub-class still needs them
	 */
	protected QueryParameters queryParameters;
	
	/**
	 * Constructor which setting the specified fields
	 * @param authenticatedUser used to set the value for {@link #currentUser}
	 * @param pollService value for {@link #pollService}
	 */
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

	/**
	 * A setter-like method for the {@link #poll}.
	 * The slightly different name is due to the fact that this method is meant to be expanded by sub-classes
	 * to actually build their poll specific components. This method here only functions as the core setter part
	 * and can as such be super-called by overriding methods.
	 * @param poll value for {@link #poll}o
	 */
	protected void loadPoll(AbstractPoll<?,?> poll) {
		this.poll = poll;
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		QueryParameters queryParameters = event.getLocation().getQueryParameters();
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
	
	/**
	 * Construct a simple view indicating that no poll has been found
	 * @return a simple view indicating that no poll has been found
	 */
	protected static VerticalLayout notFound() {
		return new ErrorMessage("Poll not found", "Sorry, the poll could not be found. Do you have the correct URL?");
	}

}
