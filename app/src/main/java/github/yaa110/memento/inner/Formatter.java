package github.yaa110.memento.inner;

import android.text.format.DateFormat;

public class Formatter {
	private static final String DATE_FORMAT = "E, LLLL d, yyyy";

	public static CharSequence formatDate() {
		return formatDate(System.currentTimeMillis());
	}

	public static CharSequence formatDate(long millis) {
		return DateFormat.format(DATE_FORMAT, millis);
	}
}
