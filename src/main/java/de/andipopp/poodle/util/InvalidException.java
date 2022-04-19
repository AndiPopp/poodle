package de.andipopp.poodle.util;

public class InvalidException extends Exception {

	/**
	 * 
	 */
	public InvalidException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidException(String message) {
		super(message);
	}
	
	

}
