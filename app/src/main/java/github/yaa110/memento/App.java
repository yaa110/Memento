// In the name of Allah
/* ----------------------------------- */

package github.yaa110.memento;

import android.app.Application;
import android.content.SharedPreferences;

import github.yaa110.memento.db.Controller;
import github.yaa110.memento.db.OpenHelper;

public class App extends Application {
	public static App instance;

	/* Preferences */
	public static boolean smartFab;
	public static int sortCategoriesBy;
	public static int sortNotesBy;

	/* Preferences' Keys */
	public static final String SMART_FAB_KEY = "a1";
	public static final String SORT_CATEGORIES_KEY = "a2";
	public static final String SORT_NOTES_KEY = "a3";

	private SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();

		// Get preferences
		prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		smartFab = prefs.getBoolean(SMART_FAB_KEY, true);
		sortCategoriesBy = sanitizeSort(prefs.getInt(SORT_CATEGORIES_KEY, Controller.SORT_DATE_DESC));
		sortNotesBy = sanitizeSort(prefs.getInt(SORT_NOTES_KEY, Controller.SORT_DATE_DESC));

		// Setup database controller
		Controller.create(getApplicationContext());

		instance = this;
	}

	private int sanitizeSort(int sortId) {
		if (sortId < 0 || sortId > 3) return Controller.SORT_DATE_DESC;
		return sortId;
	}
}
