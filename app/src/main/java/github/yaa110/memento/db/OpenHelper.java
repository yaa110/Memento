package github.yaa110.memento.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String NAME = "data.db";

	public static final String TABLE_NOTES = "notes";
	public static final String TABLE_UNDO = "undo";

	public static final String COLUMN_ID        = "_id";
	public static final String COLUMN_TITLE     = "_title";
	public static final String COLUMN_BODY      = "_body";
	public static final String COLUMN_TYPE      = "_type";
	public static final String COLUMN_DATE      = "_date";
	public static final String COLUMN_ARCHIVED  = "_archived";
	public static final String COLUMN_THEME     = "_theme";
	public static final String COLUMN_COUNTER   = "_counter";
	public static final String COLUMN_PARENT_ID = "_parent";
	public static final String COLUMN_EXTRA     = "_extra";
	public static final String COLUMN_SQL       = "_sql";

	public OpenHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Main table to store notes and categories
		db.execSQL(
			"CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + " (" +
				COLUMN_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_PARENT_ID + " INTEGER DEFAULT -1, " +
				COLUMN_TITLE     + " TEXT DEFAULT '', " +
				COLUMN_BODY      + " TEXT DEFAULT '', " +
				COLUMN_TYPE      + " INTEGER DEFAULT 0, " +
				COLUMN_ARCHIVED  + " INTEGER DEFAULT 0, " +
				COLUMN_THEME     + " INTEGER DEFAULT 0, " +
				COLUMN_COUNTER   + " INTEGER DEFAULT 0, " +
				COLUMN_DATE      + " TEXT DEFAULT '', " +
				COLUMN_EXTRA     + " TEXT DEFAULT ''" +
				")"
		);

		// Undo table to make delete queries restorable
		db.execSQL(
			"CREATE TABLE IF NOT EXISTS " + TABLE_UNDO + " (" +
				COLUMN_SQL + " TEXT" +
				")"
		);

		// A trigger to empty UNDO table, add restoring sql query to UNDO table, then delete all child notes before deleting the parent note
		db.execSQL(
			"CREATE TRIGGER IF NOT EXISTS _t1_dn BEFORE DELETE ON " + TABLE_NOTES + " BEGIN " +
				"INSERT INTO " + TABLE_UNDO + " VALUES('INSERT INTO " + TABLE_NOTES +
				"(" + COLUMN_ID + "," + COLUMN_PARENT_ID + "," + COLUMN_TITLE + "," + COLUMN_BODY + "," + COLUMN_TYPE + "," + COLUMN_ARCHIVED + "," + COLUMN_THEME + "," + COLUMN_COUNTER + "," + COLUMN_DATE + "," + COLUMN_EXTRA + ")" +
				"VALUES('||old." + COLUMN_ID + "||','||old." + COLUMN_PARENT_ID + "||','||quote(old." + COLUMN_TITLE + ")||','||quote(old." + COLUMN_BODY + ")||','||old." + COLUMN_TYPE + "||','||old." + COLUMN_ARCHIVED + "||','||old." + COLUMN_THEME + "||','||old." + COLUMN_COUNTER + "||','||quote(old." + COLUMN_DATE + ")||','||quote(old." + COLUMN_EXTRA + ")||')'); END"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
