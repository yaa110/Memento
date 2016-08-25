package github.yaa110.memento.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.model.Note;

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

	public <T extends DatabaseModel> T findNote(Class<T> cls, long id) {
		SQLiteDatabase db = helper.getReadableDatabase();

		try {
			Cursor c = db.query(
				OpenHelper.TABLE_NOTES,
				null,
				OpenHelper.COLUMN_ID + " = ?",
				new String[] {
					String.format(Locale.US, "%d", id)
				},
				null, null, null
			);

			if (c == null) return null;

			if (c.moveToFirst()) {
				try {
					return cls.getDeclaredConstructor(Cursor.class).newInstance(c);
				} catch (Exception e) {
					return null;
				}
			}

			return null;
		} finally {
			db.close();
		}
	}

	public <T extends DatabaseModel> boolean deleteNote(T note) {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			boolean isDone = db.delete(
				OpenHelper.TABLE_NOTES,
				OpenHelper.COLUMN_ID + " = ?",
				new String[] {
					String.format(Locale.US, "%d", note.id)
				}
			) > 0;

			if (isDone && note instanceof Note) {
				// Decrement the counter of category
				Cursor c = db.rawQuery(
					"UPDATE " + OpenHelper.TABLE_NOTES + " SET " + OpenHelper.COLUMN_COUNTER + " = " + OpenHelper.COLUMN_COUNTER + " - 1 WHERE " + OpenHelper.COLUMN_ID + " = ?",
					new String[]{
						String.format(Locale.US, "%d", ((Note) note).categoryId)
					}
				);

				if (c != null) {
					c.moveToFirst();
					c.close();
				}
			}

			return isDone;
		} finally {
			db.close();
		}
	}

	public <T extends DatabaseModel> boolean saveNote(T note, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			if (note.id > DatabaseModel.NEW_MODEL_ID) {
				// Update note
				return db.update(
					OpenHelper.TABLE_NOTES,
					note.getContentValues(),
					OpenHelper.COLUMN_ID + " = ?",
					new String[]{
						String.format(Locale.US, "%d", note.id)
					}
				) > 0;
			} else {
				// Create a new note
				note.id = db.insert(
					OpenHelper.TABLE_NOTES,
					null,
					values
				);

				boolean isDone = note.id > DatabaseModel.NEW_MODEL_ID;

				if (isDone && note instanceof Note) {
					// Increment the counter of category
					Cursor c = db.rawQuery(
						"UPDATE " + OpenHelper.TABLE_NOTES + " SET " + OpenHelper.COLUMN_COUNTER + " = " + OpenHelper.COLUMN_COUNTER + " + 1 WHERE " + OpenHelper.COLUMN_ID + " = ?",
						new String[]{
							String.format(Locale.US, "%d", ((Note) note).categoryId)
						}
					);

					if (c != null) {
						c.moveToFirst();
						c.close();
					}
				}

				return isDone;
			}
		} catch (Exception e) {
			return false;
		} finally {
			db.close();
		}
	}
}
