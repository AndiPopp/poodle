package de.andipopp.poodle.data.entity.polls;

import java.util.Comparator;

/**
 * Compares options by the sum of {@link AnswerType#YES} and {@link AnswerType#IF_NEED_BE} answer.
 * If the sum is equal, the sum of {@link AnswerType#YES} options is a tie breaker. If this is still equal,
 * the sum of {@link AnswerType#NO} votes is the final tie breaker.
 * @author Andi Popp
 *
 */
public class OptionComparatorByPositiveVotes implements Comparator<AbstractOption<?, ?>> {

	public static final OptionComparatorByPositiveVotes GET = new OptionComparatorByPositiveVotes();
	
	@Override
	public int compare(AbstractOption<?, ?> arg0, AbstractOption<?, ?> arg1) {
		int result;
		
		result = (arg0.countAnswers(AnswerType.YES) + arg0.countAnswers(AnswerType.IF_NEED_BE))
				- (arg1.countAnswers(AnswerType.YES) + arg1.countAnswers(AnswerType.IF_NEED_BE));
		if (result != 0) return -result; //Negative because we want to sort descending
		
		result = arg0.countAnswers(AnswerType.YES) - arg1.countAnswers(AnswerType.YES);
		if (result != 0) return -result;
		
		result = arg1.countAnswers(AnswerType.NO) - arg0.countAnswers(AnswerType.NO);
		return -result;
	}

	
}
