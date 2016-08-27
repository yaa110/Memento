package github.yaa110.memento.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
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

	/**
	 * The singleton instance of Controller class
	 */
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

	/**
	 * Instantiates the singleton instance of Controller class
	 * @param context the application context
	 */
	public static void create(Context context) {
		instance = new Controller(context);
	}

	/**
	 * Reads data from json array
	 * @param json an array of json objects
	 * @throws Exception
	 */
	public void readBackup(JSONArray json) throws Exception {
		SQLiteDatabase db = helper.getReadableDatabase();

		try {
			int length = json.length();
			for (int i = 0; i < length; i++) {
				JSONObject item = json.getJSONObject(i);

				ContentValues values = new ContentValues();
				values.put(OpenHelper.COLUMN_ID, item.getLong(OpenHelper.COLUMN_ID));
				values.put(OpenHelper.COLUMN_TITLE, item.getString(OpenHelper.COLUMN_TITLE));
				values.put(OpenHelper.COLUMN_BODY, item.getString(OpenHelper.COLUMN_BODY));
				values.put(OpenHelper.COLUMN_TYPE, item.getInt(OpenHelper.COLUMN_TYPE));
				values.put(OpenHelper.COLUMN_DATE, item.getString(OpenHelper.COLUMN_DATE));
				values.put(OpenHelper.COLUMN_ARCHIVED, item.getInt(OpenHelper.COLUMN_ARCHIVED));
				values.put(OpenHelper.COLUMN_THEME, item.getInt(OpenHelper.COLUMN_THEME));
				values.put(OpenHelper.COLUMN_COUNTER, item.getInt(OpenHelper.COLUMN_COUNTER));
				values.put(OpenHelper.COLUMN_PARENT_ID, item.getLong(OpenHelper.COLUMN_PARENT_ID));
				values.put(OpenHelper.COLUMN_EXTRA, item.getString(OpenHelper.COLUMN_EXTRA));

				db.replace(
					OpenHelper.TABLE_NOTES,
					null,
					values
				);
			}
		} finally {
			db.close();
		}
	}

	/**
	 * Writes data to file
	 * @param fos an object of FileOutputStream
	 * @throws Exception
	 */
	public void writeBackup(FileOutputStream fos) throws Exception {
		SQLiteDatabase db = helper.getReadableDatabase();

		try {
			Cursor c = db.query(
				OpenHelper.TABLE_NOTES,
				null, null, null, null, null, null
			);

			if (c != null) {
				boolean needComma = false;
				while (c.moveToNext()) {
					if (needComma) {
						fos.write(",".getBytes("UTF-8"));
					} else {
						needComma = true;
					}

					JSONObject item = new JSONObject();
					item.put(OpenHelper.COLUMN_ID, c.getLong(c.getColumnIndex(OpenHelper.COLUMN_ID)));
					item.put(OpenHelper.COLUMN_TITLE, c.getString(c.getColumnIndex(OpenHelper.COLUMN_TITLE)));
					item.put(OpenHelper.COLUMN_BODY, c.getString(c.getColumnIndex(OpenHelper.COLUMN_BODY)));
					item.put(OpenHelper.COLUMN_TYPE, c.getInt(c.getColumnIndex(OpenHelper.COLUMN_TYPE)));
					item.put(OpenHelper.COLUMN_DATE, c.getString(c.getColumnIndex(OpenHelper.COLUMN_DATE)));
					item.put(OpenHelper.COLUMN_ARCHIVED, c.getInt(c.getColumnIndex(OpenHelper.COLUMN_ARCHIVED)));
					item.put(OpenHelper.COLUMN_THEME, c.getInt(c.getColumnIndex(OpenHelper.COLUMN_THEME)));
					item.put(OpenHelper.COLUMN_COUNTER, c.getInt(c.getColumnIndex(OpenHelper.COLUMN_COUNTER)));
					item.put(OpenHelper.COLUMN_PARENT_ID, c.getLong(c.getColumnIndex(OpenHelper.COLUMN_PARENT_ID)));
					item.put(OpenHelper.COLUMN_EXTRA, c.getString(c.getColumnIndex(OpenHelper.COLUMN_EXTRA)));

					fos.write(item.toString().getBytes("UTF-8"));
				}

				c.close();
			}
		} finally {
			db.close();
		}
	}

	/**
	 * Reads all notes or categories from database
	 * @param cls the class of the model type
	 * @param columns the columns must be returned from the query
	 * @param where the where clause of the query.
	 * @param whereParams the parameters of where clause.
	 * @param sortId the sort id of categories or notes
	 * @param <T> a type which extends DatabaseModel
	 * @return a list of notes or categories
	 */
	public <T extends DatabaseModel> ArrayList<T> findNotes(Class<T> cls, String[] columns, String where, String[] whereParams, int sortId) {
		ArrayList<T> items = new ArrayList<>();

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

			if (c != null) {
				while (c.moveToNext()) {
					try {
						items.add(cls.getDeclaredConstructor(Cursor.class).newInstance(c));
					} catch (Exception ignored) {
					}
				}

				c.close();
			}

			return items;
		} finally {
			db.close();
		}
	}

	/**
	 * Reads a note or category from the database
	 * @param cls the class of the model type
	 * @param id primary key of note or category
	 * @param <T> a type which extends DatabaseModel
	 * @return a new object of T type
	 */
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

	/**
	 * Change the amount of category counter
	 * @param categoryId the id of category
	 * @param amount to be added (negative or positive)
	 */
	public void addCategoryCounter(long categoryId, int amount) {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			Cursor c = db.rawQuery(
				"UPDATE " + OpenHelper.TABLE_NOTES + " SET " + OpenHelper.COLUMN_COUNTER + " = " + OpenHelper.COLUMN_COUNTER + " + ? WHERE " + OpenHelper.COLUMN_ID + " = ?",
				new String[]{
					String.format(Locale.US, "%d", amount),
					String.format(Locale.US, "%d", categoryId)
				}
			);

			if (c != null) {
				c.moveToFirst();
				c.close();
			}
		} finally {
			db.close();
		}
	}

	/**
	 * Restores last deleted notes
	 */
	public void undoDeletion() {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			Cursor c = db.query(
				OpenHelper.TABLE_UNDO,
				null, null, null, null, null, null
			);

			if (c != null) {
				while (c.moveToNext()) {
					String query = c.getString(c.getColumnIndex(OpenHelper.COLUMN_SQL));
					if (query != null) {
						Cursor nc = db.rawQuery(
							query,
							null
						);

						if (nc != null) {
							nc.moveToFirst();
							nc.close();
						}
					}
				}

				c.close();
			}

			clearUndoTable(db);
		} finally {
			db.close();
		}
	}

	/**
	 * Clears the undo table
	 * @param db an object of writable SQLiteDatabase
	 */
	public void clearUndoTable(SQLiteDatabase db) {
		Cursor uc = db.rawQuery("DELETE FROM " + OpenHelper.TABLE_UNDO, null);
		if (uc != null) {
			uc.moveToFirst();
			uc.close();
		}
	}

	/**
	 * Deletes a note or category (and its children) from the database
	 * @param ids a list of the notes' IDs
	 * @param categoryId the id of parent category
	 */
	public void deleteNotes(String[] ids, long categoryId) {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			clearUndoTable(db);

			StringBuilder where = new StringBuilder();
			StringBuilder childWhere = new StringBuilder();

			boolean needOR = false;
			for (int i = 0; i < ids.length; i++) {
				if (needOR) {
					where.append(" OR ");
					childWhere.append(" OR ");
				} else {
					needOR = true;
				}
				where.append(OpenHelper.COLUMN_ID).append(" = ?");
				childWhere.append(OpenHelper.COLUMN_PARENT_ID).append(" = ?");
			}

			int count = db.delete(
				OpenHelper.TABLE_NOTES,
				where.toString(),
				ids
			);

			if (categoryId == DatabaseModel.NEW_MODEL_ID) {
				db.delete(
					OpenHelper.TABLE_NOTES,
					childWhere.toString(),
					ids
				);
			} else {
				Cursor c = db.rawQuery(
					"UPDATE " + OpenHelper.TABLE_NOTES + " SET " + OpenHelper.COLUMN_COUNTER + " = " + OpenHelper.COLUMN_COUNTER + " - ? WHERE " + OpenHelper.COLUMN_ID + " = ?",
					new String[]{
						String.format(Locale.US, "%d", count),
						String.format(Locale.US, "%d", categoryId)
					}
				);

				if (c != null) {
					c.moveToFirst();
					c.close();
				}
			}
		} finally {
			db.close();
		}
	}

	/**
	 * Inserts or updates a note or category in the database and increments the counter
	 * of category if the deleted object is an instance of Note class
	 * @param note the object of type T
	 * @param values ContentValuse of the object to be inserted or updated
	 * @param <T> a type which extends DatabaseModel
	 * @return the id of saved note
	 */
	public <T extends DatabaseModel> long saveNote(T note, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			if (note.id > DatabaseModel.NEW_MODEL_ID) {
				// Update note
				db.update(
					OpenHelper.TABLE_NOTES,
					note.getContentValues(),
					OpenHelper.COLUMN_ID + " = ?",
					new String[]{
						String.format(Locale.US, "%d", note.id)
					}
				);
				return note.id;
			} else {
				// Create a new note
				note.id = db.insert(
					OpenHelper.TABLE_NOTES,
					null,
					values
				);

				if (note instanceof Note) {
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

				return note.id;
			}
		} catch (Exception e) {
			return DatabaseModel.NEW_MODEL_ID;
		} finally {
			db.close();
		}
	}
}
