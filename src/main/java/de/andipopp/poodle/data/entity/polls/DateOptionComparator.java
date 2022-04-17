package de.andipopp.poodle.data.entity.polls;

import java.util.Comparator;

public class DateOptionComparator implements Comparator<DateOption> {
	
	@Override
	public int compare(DateOption arg0, DateOption arg1) {
		int result = 0;
		
		//first check if UUID are the same
		if (arg0.getId() != null && arg0.getId().equals(arg1.getId())) return 0;
		
		//compare start date first
		result = arg0.getStart().compareTo(arg1.getStart());
		if (result != 0) return result;
		
		//if equal, sort by end date
		result = arg0.getEnd().compareTo(arg1.getEnd());
		if (result != 0) return result;
		
		//if still equal, sort by title
		String arg0aux = "";
		String arg1aux = "";
		if (arg0.getTitle() != null) arg0aux = arg0.getTitle();
		if (arg1.getTitle() != null) arg0aux = arg1.getTitle();
		result = arg0aux.compareTo(arg1aux);
		if (result != 0) return result;
		
		//if still equal, sort by location
		arg0aux = "";
		arg1aux = "";
		if (arg0.getLocation() != null) arg0aux = arg0.getLocation();
		if (arg1.getLocation() != null) arg0aux = arg1.getLocation();
		result = arg0aux.compareTo(arg1aux);
		if (result != 0) return result;
		
		//else sort by UUID
		return arg0.getId().compareTo(arg1.getId());
		
		//TODO there are some more NullPointerExceptions to avoid
	}
}
