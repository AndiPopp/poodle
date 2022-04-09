package de.andipopp.poodle.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class JSoupUtils {

	public static final Safelist BASIC = Safelist.basic();
	
	public static final Safelist NONE = Safelist.none();
	
	public static String cleanBasic(String s) {
		return Jsoup.clean(s, BASIC);
	}
	
	public static String cleanNone(String s) {
		return Jsoup.clean(s, NONE);
	}
}
