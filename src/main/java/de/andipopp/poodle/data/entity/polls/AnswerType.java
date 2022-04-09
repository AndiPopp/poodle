package de.andipopp.poodle.data.entity.polls;

/**
 * An answer to a single option
 * @author Andi Popp
 *
 */
public enum AnswerType {

	/**
	 * No answer
	 */
	NONE,
	
	/**
	 * Yes
	 */
	YES, 
	
	/**
	 * No
	 */
	NO, 
	
	/**
	 * If need be
	 */
	IF_NEED_BE;
	
	/**
	 * Get the next answer type given the answer settings
	 * @param allowAbstain true if abstaining is allowed
	 * @param allowIfNeedBe true if {@link AnswerType#IF_NEED_BE} is an allowed option
	 * @return the next viable answer in sequence
	 */
	public AnswerType nextAnswer(boolean allowAbstain, boolean allowIfNeedBe) {
		switch (this) {
		case IF_NEED_BE:
			return NO;
		case NO:
			if (allowAbstain) return NONE;
			else return YES;
		case NONE:
			return YES;
		case YES:
			if (allowIfNeedBe) return IF_NEED_BE;
			else return NO;
		default:
			return this;
		}
	}
	
}
