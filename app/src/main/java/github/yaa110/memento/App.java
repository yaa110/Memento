// In the name of Allah
/* ----------------------------------- */

package github.yaa110.memento;

import android.app.Application;
import android.content.SharedPreferences;

public class App extends Application {
	public static App instance;

	/* Preferences */
	public static boolean smartFab;

	/* Preferences' Keys */
	public static final String SMART_FAB_KEY = "k1";

	private SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		smartFab = prefs.getBoolean(SMART_FAB_KEY, true);

		instance = this;
	}
}
