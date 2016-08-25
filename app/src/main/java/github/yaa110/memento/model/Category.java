package github.yaa110.memento.model;

import android.content.ContentValues;
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

	/**
	 * Instantiates a new object of Category class using the data retrieved from database.
	 * @param c cursor object returned from a database query
	 */
	public Category(Cursor c) {
		super(c);
		this.theme = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_THEME));
		this.counter = c.getInt(c.getColumnIndex(OpenHelper.COLUMN_COUNTER));
	}

	/**
	 * @return ContentValue object to be saved or updated
	 */
	@Override
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();

		if (id == DatabaseModel.NEW_MODEL_ID) {
			values.put(OpenHelper.COLUMN_TYPE, type);
			values.put(OpenHelper.COLUMN_DATE, createdAt);
			values.put(OpenHelper.COLUMN_COUNTER, counter);
			values.put(OpenHelper.COLUMN_ARCHIVED, isArchived);
		}

		values.put(OpenHelper.COLUMN_TITLE, title);
		values.put(OpenHelper.COLUMN_THEME, theme);

		return values;
	}


	/**
	 * @return color of the theme
	 */
	public int getThemeColor() {
		return Color.parseColor(colors[theme]);
	}

	/**
	 * Reads a category by its id
	 * @param id primary key of category
	 * @return the category object or null if it was not found
	 */
	public static Category find(long id) {
		return Controller.instance.findNote(Category.class, id);
	}

	/**
	 * Reads all categories
	 * @param items a list of categories which is populated by database
	 * @param isArchived determines if the category is archived
	 */
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
