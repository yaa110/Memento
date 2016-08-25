package github.yaa110.memento.model;

import android.database.Cursor;
import android.graphics.Color;

import github.yaa110.memento.db.OpenHelper;

public class Category extends DatabaseModel {
	public static final int THEME_RED       = 0;
	public static final int THEME_PINK      = 1;
	public static final int THEME_PURPLE    = 2;
	public static final int THEME_BLUE      = 3;
	public static final int THEME_CYAN      = 4;
	public static final int THEME_TEAL      = 5;
	public static final int THEME_GREAN     = 6;
	public static final int THEME_AMBER     = 7;
	public static final int THEME_ORANGE    = 8;
	public static final int THEME_BLUE_GRAY = 9;

	private String[] colors = {
		"#F44336",
		"#E91E63",
		"#9C27B0",
		"#2196F3",
		"#0097A7",
		"#00796B",
		"#4CAF50",
		"#FFA000",
		"#FF5722",
		"#607D8B"
	};

	public int theme;
	public int counter;

	public Category() {}

	public Category(Cursor c, boolean isArchived) {
		this.id = c.getLong(c.getColumnIndex(OpenHelper.COLUMN_ID));
		this.type = DatabaseModel.TYPE_CATEGORY;
		this.title = c.getString(c.getColumnIndex(OpenHelper.COLUMN_TITLE));
		try {
			this.createdAt = Long.parseLong(c.getString(c.getColumnIndex(OpenHelper.COLUMN_DATE)));
		} catch (NumberFormatException nfe) {
			this.createdAt = 0;
		}
		this.isArchived = isArchived;
		this.theme = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_THEME));
		this.counter = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_COUNTER));
	}

	public int getThemeColor() {
		return Color.parseColor(colors[theme]);
	}
}
