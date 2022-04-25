package de.andipopp.poodle.views.components;

import java.io.File;
import java.util.UUID;

import com.vaadin.flow.component.avatar.Avatar;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.servlets.AvatarImageServlet;

/**
 * Static methods to create {@link Avatar}s for Poodle onbjects
 * @author Andi Popp
 *
 */
public class PoodleAvatar {

	/**
	 * Types of objects which can have avatars
	 * @author Andi Popp
	 *
	 */
	public enum Type {
		/**
		 * Corresponding to {@link AbstractPoll}
		 */
		POLL,
		
		/**
		 * Corresponding to {@link User}
		 */
		USER
	}

	/**
	 * Create an avatar for a poll object
	 * @param poll the poll for which to create the avatar
	 * @return the constructed avatar
	 */
	public static Avatar forPoll(AbstractPoll<?, ?> poll) {
		return forObject(Type.POLL, poll.getOwner(), poll.getTitle(), poll.getId());
	}
	
	/**
	 * Create an avatar for a user obejct
	 * @param user the user for which to create the avatar
	 * @return the constructed avatar
	 */
	public static Avatar forUser(User user) {
		return forObject(Type.USER, null, user.getDisplayName(), user.getId());
	}
	
	/**
	 * This method constructs the actual avatar
	 * @param type the type of object for which to create an avatar
	 * @param owner an optional owner for this type (can be null)
	 * @param name the name for the avatar (initials displayed if no picture present)
	 * @param id the ID of the object for which to create the avatar
	 * @return the constructed avatar
	 */
	private static Avatar forObject(Type type, User owner, String name, UUID id) {

		String imagePath = "";
		
		switch (type) {
		case POLL:
			imagePath = Config.getCurrent().getPollImagePath();
			break;
		case USER:
			imagePath = Config.getCurrent().getUserImagePath();
			break;
		default:
			break;
		
		}
		
		imagePath += System.getProperty("file.separator") 
				+ UUIDUtils.uuidToBase64url(id)
				+ ImageUpload.FILE_EXTENSION;
		
		File imageFile = new File(imagePath);
		
		if (imageFile.exists()) {
			return new Avatar(name, AvatarImageServlet.SUB_FOLDER + "?type=" + type.toString().toLowerCase() + "&name="+imageFile.getName());	
		}
		else if (owner != null) {
			return owner.getAvatar();
		}
		else {
			return new Avatar(name);
		}
	}
	
	public static String avatarImagePath(Type type, UUID id) {
		return AvatarImageServlet.SUB_FOLDER + "?type=" + type.toString().toLowerCase() 
				+ "&name=" + UUIDUtils.uuidToBase64url(id) + ImageUpload.FILE_EXTENSION;
	}
	
	
	
	
	
}
