package github.yaa110.memento.model;

public class DatabaseModel {
	public static final int TYPE_CATEGORY = 0;
	public static final int TYPE_NOTE_SIMPLE = 1;
	public static final int TYPE_NOTE_DRAWING = 2;

	public long id;
	public int type;
	public String title;
	public long createdAt;
	public boolean isArchived;
	public boolean isDeleted;
}
