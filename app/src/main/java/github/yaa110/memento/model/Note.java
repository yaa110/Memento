package github.yaa110.memento.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Locale;

import github.yaa110.memento.App;
import github.yaa110.memento.db.Controller;
import github.yaa110.memento.db.OpenHelper;

public class Note extends DatabaseModel {
	public long categoryId;
	public String body;

	public Note() {}

	public Note(Cursor c) {
		super(c);
		this.categoryId = c.getLong(c.getColumnIndex(OpenHelper.COLUMN_PARENT_ID));
		this.body = c.getString(c.getColumnIndex(OpenHelper.COLUMN_BODY));
	}

	public static Note find(long id) {
		return Controller.instance.findNote(Note.class, id);
	}

	public static void all(ArrayList<Note> items, boolean isArchived) {
		Controller.instance.findNotes(
			Note.class,
			items,
			new String[] {
				OpenHelper.COLUMN_ID,
				OpenHelper.COLUMN_TITLE,
				OpenHelper.COLUMN_DATE,
				OpenHelper.COLUMN_TYPE,
				OpenHelper.COLUMN_ARCHIVED,
				OpenHelper.COLUMN_PARENT_ID,
				OpenHelper.COLUMN_BODY
			},
			OpenHelper.COLUMN_TYPE + " != ? AND " + OpenHelper.COLUMN_ARCHIVED + " = ?",
			new String[]{
				String.format(Locale.US, "%d", DatabaseModel.TYPE_CATEGORY),
				isArchived ? "1" : "0"
			},
			App.sortNotesBy
		);
	}
}
