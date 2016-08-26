package github.yaa110.memento.inner;

import android.text.format.DateFormat;

public class Formatter {
	private static final String DATE_FORMAT = "E, LLLL d, yyyy";
	private static final String SHORT_DATE_FORMAT = "LLL d, yyyy";

	public static CharSequence formatDate() {
		return formatDate(System.currentTimeMillis());
	}

	public static CharSequence formatDate(long millis) {
		return DateFormat.format(DATE_FORMAT, millis);
	}

	public static CharSequence formatShortDate(long millis) {
		return DateFormat.format(SHORT_DATE_FORMAT, millis);
	}
}
