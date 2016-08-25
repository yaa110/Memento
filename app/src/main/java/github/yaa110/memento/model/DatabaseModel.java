package github.yaa110.memento.model;

import android.database.Cursor;

import github.yaa110.memento.db.Controller;
import github.yaa110.memento.db.OpenHelper;

public class DatabaseModel {
	public static final int TYPE_CATEGORY = 0;
	public static final int TYPE_NOTE_SIMPLE = 1;
	public static final int TYPE_NOTE_DRAWING = 2;

	public long id;
	public int type;
	public String title;
	public long createdAt;
	public boolean isArchived;

	public DatabaseModel() {}

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

	public boolean delete() {
		return Controller.instance.deleteNote(id);
	}
}
