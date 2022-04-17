package de.andipopp.poodle.data.entity;

import java.util.UUID;

/**
 * Interface for classes with UUID
 * @author Andi Popp
 *
 */
public interface HasUuid {

	/**
	 * Get the UUID
	 * @return the UUID
	 */
	public UUID getId();
}
