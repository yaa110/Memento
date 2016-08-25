package github.yaa110.memento.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

import github.yaa110.memento.App;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.model.DatabaseModel;

public class Controller {
	public static final int SORT_TITLE_ASC = 0;
	public static final int SORT_TITLE_DESC = 1;
	public static final int SORT_DATE_ASC = 2;
	public static final int SORT_DATE_DESC = 3;

	public static Controller instance = null;

	private SQLiteOpenHelper helper;
	private String[] sorts = {
		OpenHelper.COLUMN_TITLE + " ASC",
		OpenHelper.COLUMN_TITLE + " DESC",
		OpenHelper.COLUMN_ID + " ASC",
		OpenHelper.COLUMN_ID + " DESC",
	};

	private Controller(Context context) {
		helper = new OpenHelper(context);
	}

	public static void create(Context context) {
		instance = new Controller(context);
	}

	public void findCategories(ArrayList<Category> items) {
		if (items == null) {
			items = new ArrayList<>();
		} else {
			items.clear();
		}

		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor c = db.query(
			OpenHelper.TABLE_NOTES,
			new String[] {
				OpenHelper.COLUMN_ID,
				OpenHelper.COLUMN_TITLE,
				OpenHelper.COLUMN_THEME,
				OpenHelper.COLUMN_DATE,
				OpenHelper.COLUMN_COUNTER
			},
			OpenHelper.COLUMN_TYPE + " = ? AND " + OpenHelper.COLUMN_ARCHIVED + " = ?",
			new String[]{
				String.format(Locale.US, "%d", DatabaseModel.TYPE_CATEGORY),
				"0"
			},
			null, null,
			sorts[App.sortCategoriesBy]
		);

		db.close();
	}
}
