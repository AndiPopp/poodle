package de.andipopp.poodle.data.entity.polls;

import java.util.Comparator;

public class DateOptionComparator implements Comparator<DateOption> {

	@Override
	public int compare(DateOption arg0, DateOption arg1) {
		//first check if UUID are the same
		if (arg0.getId().equals(arg1.getId())) return 0;
		
		//compare start date first
		int result = arg0.getStart().compareTo(arg1.getStart());
		if (result != 0) return result;
		//if equal, sort by end date
		result = arg0.getEnd().compareTo(arg1.getEnd());
		if (result != 0) return result;
		//if still equal, sort by location
		result = arg0.getLocation().compareTo(arg1.getLocation());
		if (result != 0) return result;
		//else sort by RUID
		return arg0.getId().compareTo(arg1.getId());
	}
}
