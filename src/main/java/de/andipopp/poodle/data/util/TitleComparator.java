package de.andipopp.poodle.data.util;

import java.util.Comparator;

/**
 * Comparator for {@link HasUuidAndTitle} objects.
 * <p>This will simply compare by title. If {@link #ensureConsistentWithEquals} is <code>true</code>,
 * it will continue to compare in case titles are identical, first be UUID then by system hashcode. </p>
 * @author Andi Popp
 *
 */
public class TitleComparator implements Comparator<HasUuidAndTitle> {

	/**
	 * Make sure the comparator is consistent with equals as object
	 */
	private boolean ensureConsistentWithEquals = true;
	
	/**
	 * Create new comparator with standard features
	 */
	public TitleComparator() {	}

	/**
	 * Create new comparator setting {@link #ensureConsistentWithEquals}
	 * @param ensureConsistentWithEquals value for {@link #ensureConsistentWithEquals}
	 */
	public TitleComparator(boolean ensureConsistentWithEquals) {
		this.ensureConsistentWithEquals = ensureConsistentWithEquals;
	}

	@Override
	public int compare(HasUuidAndTitle arg0, HasUuidAndTitle arg1) {
		int result;

		String arg0aux = "";
		String arg1aux = "";
		if (arg0.getTitle() != null) arg0aux = arg0.getTitle();
		if (arg1.getTitle() != null) arg0aux = arg1.getTitle();
		result = arg0aux.compareTo(arg1aux);
		//if we don't need to ensure consistent with equals, we can stop here regardless of the result
		if (result != 0 || !ensureConsistentWithEquals) return result;
		
		//else sort by UUID, events with UUID are always considered smaller than events without
		if (arg0.getId() != null && arg1.getId() != null) {
			result = arg0.getId().compareTo(arg1.getId());
		} else if (arg0.getId() != null && arg1.getId() == null) {
			return -1;
		} else if (arg0.getId() != null && arg1.getId() == null) {
			return 1;
		}
		if (result != 0) return result;
		
		//if still equal, compare by system identity hashcode
		Integer h0 = System.identityHashCode(arg0);
		Integer h1 = System.identityHashCode(arg1);
		return h0.compareTo(h1);
	}

}
