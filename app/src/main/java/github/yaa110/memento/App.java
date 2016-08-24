// In the name of Allah
/* ----------------------------------- */

package github.yaa110.memento;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

public class App extends Application {
	public static App instance;

	/* Preferences */
	public static boolean nightMode = false;

	/* Preferences' Keys */
	private static final String NIGHT_MODE_KEY = "k1";

	private SharedPreferences prefs;

	@Override
	public void onCreate() {
		if (nightMode) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}

		super.onCreate();

		prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		nightMode = prefs.getBoolean(NIGHT_MODE_KEY, false);

		instance = this;
	}

}
