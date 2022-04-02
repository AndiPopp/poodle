/**
 * 
 */
package de.andipopp.poodle.util;

/**
 * Exception indicating that a translation into UUID failed
 * @author Andi Popp
 *
 */
public class NotAUuidException extends Exception {
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new {@link NotAUuidException} with a message and a cause
	 * @param message the message
	 * @param cause the cause
	 */
	public NotAUuidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a new {@link NotAUuidException} with a message 
	 * @param message the message
	 */
	public NotAUuidException(String message) {
		super(message);
	}
	
	
}
