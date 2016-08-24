// In the name of Allah
/* ----------------------------------- */

package github.yaa110.memento;

import android.app.Application;
import android.content.SharedPreferences;

public class App extends Application {
	public static App instance;

	public static final String DATE_FORMAT = "E, LLLL d, yyyy";

	/* Preferences */

	/* Preferences' Keys */

	private SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

		instance = this;
	}

}
