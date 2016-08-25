package github.yaa110.memento.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

import github.yaa110.memento.model.DatabaseModel;

@SuppressWarnings("TryFinallyCanBeTryWithResources")
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

	public <T extends DatabaseModel> void findNotes(Class<T> cls, ArrayList<T> items, String[] columns, String where, String[] whereParams, int sortId) {
		if (items == null) {
			items = new ArrayList<>();
		} else {
			items.clear();
		}

		SQLiteDatabase db = helper.getReadableDatabase();

		try {
			Cursor c = db.query(
				OpenHelper.TABLE_NOTES,
				columns,
				where,
				whereParams,
				null, null,
				sorts[sortId]
			);

			if (c == null) return;

			while (c.moveToNext()) {
				try {
					items.add(cls.getDeclaredConstructor(Cursor.class).newInstance(c));
				} catch (Exception ignored) {
				}
			}

			c.close();
		} finally {
			db.close();
		}
	}

	public boolean deleteNote(long id) {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			return db.delete(
				OpenHelper.TABLE_NOTES,
				OpenHelper.COLUMN_ID + " = ?",
				new String[] {
					String.format(Locale.US, "%d", id)
				}
			) > 0;
		} finally {
			db.close();
		}
	}
}
