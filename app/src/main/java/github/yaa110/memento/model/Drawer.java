package github.yaa110.memento.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class Drawer {
	public static final int TYPE_SPLITTER = 0;
	public static final int TYPE_ABOUT = 1;
	public static final int TYPE_BACKUP = 2;
	public static final int TYPE_RESTORE = 3;
	public static final int TYPE_SETTINGS = 4;

	public int type;
	@DrawableRes
	public int resId;
	@StringRes
	public int title;

	public Drawer() {}

	public Drawer(int type, @DrawableRes int resId, @StringRes int title) {
		this.type = type;
		this.resId = resId;
		this.title = title;
	}

	public static Drawer divider() {
		Drawer splitter = new Drawer();
		splitter.type = TYPE_SPLITTER;
		return splitter;
	}
}
