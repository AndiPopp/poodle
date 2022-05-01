/**
 * 
 */
package de.andipopp.poodle.data.util;

/**
 * Interface for class with UUID and title
 * @author Andi Popp
 *
 */
public interface HasUuidAndTitle extends HasUuid {

	/**
	 * Get the title
	 * @return the title
	 */
	public String getTitle();
	
}
