package github.yaa110.memento.model;

import android.database.Cursor;

import github.yaa110.memento.db.Controller;

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

	public DatabaseModel(Cursor c) {}

	public boolean delete() {
		return Controller.instance.deleteNote(id);
	}
}
