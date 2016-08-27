package github.yaa110.memento.model;

public class Folder {
	public String name;
	public String path;
	public boolean isBack;
	public boolean isDirectory;

	public Folder(String name, String path, boolean isBack) {
		this.name = name;
		this.path = path;
		this.isBack = isBack;
		this.isDirectory = true;
	}

	public Folder(String name, String path, boolean isBack, boolean isDirectory) {
		this.name = name;
		this.path = path;
		this.isBack = isBack;
		this.isDirectory = isDirectory;
	}
}
