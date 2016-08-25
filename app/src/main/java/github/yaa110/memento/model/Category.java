package github.yaa110.memento.model;

import android.database.Cursor;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Locale;

import github.yaa110.memento.App;
import github.yaa110.memento.db.Controller;
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

	public Category(Cursor c) {
		super(c);
		this.theme = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_THEME));
		this.counter = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_COUNTER));
	}

	public int getThemeColor() {
		return Color.parseColor(colors[theme]);
	}

	public static Category find(long id) {
		return Controller.instance.findNote(Category.class, id);
	}

	public static void all(ArrayList<Category> items, boolean isArchived) {
		Controller.instance.findNotes(
			Category.class,
			items,
			new String[] {
				OpenHelper.COLUMN_ID,
				OpenHelper.COLUMN_TITLE,
				OpenHelper.COLUMN_DATE,
				OpenHelper.COLUMN_TYPE,
				OpenHelper.COLUMN_ARCHIVED,
				OpenHelper.COLUMN_THEME,
				OpenHelper.COLUMN_COUNTER
			},
			OpenHelper.COLUMN_TYPE + " = ? AND " + OpenHelper.COLUMN_ARCHIVED + " = ?",
			new String[]{
				String.format(Locale.US, "%d", DatabaseModel.TYPE_CATEGORY),
				isArchived ? "1" : "0"
			},
			App.sortCategoriesBy
		);
	}
}
