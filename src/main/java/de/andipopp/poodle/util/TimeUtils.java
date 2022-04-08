package de.andipopp.poodle.util;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

public class TimeUtils {
	
	public static SortedSet<TimeZone> getSupportedTimeZonesByOffset(boolean includeEtcs) {
		SortedSet<TimeZone> set = new TreeSet<>((arg0, arg1) ->  {
			int diffOffset = arg0.getRawOffset() - arg1.getRawOffset();
			if (diffOffset != 0) return diffOffset;
			return arg0.getID().compareTo(arg1.getID());
		});
		
		for(String id :TimeZone.getAvailableIDs()) {
			if (includeEtcs || !id.startsWith("Etc")) set.add(TimeZone.getTimeZone(id));
		}
		return set;
	}
	
	public static List<ZoneId> getSupportedZoneIdsInAlphabeticalOrder() {
		SortedSet<String> ids = new TreeSet<String>();
		ids.addAll(ZoneId.getAvailableZoneIds());
		
		List<ZoneId> zoneIds = new ArrayList<>(ids.size());
		for(String id : ids) {
			zoneIds.add(ZoneId.of(id));
		}
		return zoneIds;
	}
}
