package github.yaa110.memento.model;

import android.content.ContentValues;
import android.database.Cursor;

import github.yaa110.memento.db.Controller;
import github.yaa110.memento.db.OpenHelper;

abstract public class DatabaseModel {
	public static final int TYPE_CATEGORY = 0;
	public static final int TYPE_NOTE_SIMPLE = 1;
	public static final int TYPE_NOTE_DRAWING = 2;

	public static final long NEW_MODEL_ID = -1;

	public long id = NEW_MODEL_ID;
	public int type;
	public String title;
	public long createdAt;
	public boolean isArchived;

	public DatabaseModel() {}

	/**
	 * Instantiates a new object of DatabaseModel class using the data retrieved from database.
	 * @param c cursor object returned from a database query
	 */
	public DatabaseModel(Cursor c) {
		this.id = c.getLong(c.getColumnIndex(OpenHelper.COLUMN_ID));
		this.type = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_TYPE));
		this.title = c.getString(c.getColumnIndex(OpenHelper.COLUMN_TITLE));
		try {
			this.createdAt = Long.parseLong(c.getString(c.getColumnIndex(OpenHelper.COLUMN_DATE)));
		} catch (NumberFormatException nfe) {
			this.createdAt = System.currentTimeMillis();
		}
		this.isArchived = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_ARCHIVED)) == 1;
	}

	/**
	 * Inserts or updates a note or category
	 * @return true if the note is saved.
	 */
	public boolean save() {
		return Controller.instance.saveNote(this, getContentValues());
	}

	/**
	 * Deletes a note or category
	 * @return true if the note is deleted.
 	 */
	public boolean delete() {
		return Controller.instance.deleteNote(this);
	}

	/**
	 * 	Toggle archived state and
	 * 	@return true if the action is completed.
	 */
	public boolean toggle() {
		ContentValues values = new ContentValues();
		values.put(OpenHelper.COLUMN_ARCHIVED, !isArchived);

		if (Controller.instance.saveNote(this, values)) {
			isArchived = !isArchived;
			return true;
		}

		return false;
	}

	/**
	 * @return ContentValue object to be saved or updated
	 */
	abstract public ContentValues getContentValues();
}
