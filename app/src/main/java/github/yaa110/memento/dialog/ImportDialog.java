package github.yaa110.memento.dialog;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import github.yaa110.memento.App;
import github.yaa110.memento.R;
import github.yaa110.memento.adapter.FolderAdapter;
import github.yaa110.memento.model.Folder;
import github.yaa110.memento.widget.FixedHeightRecyclerView;

public class ImportDialog extends DialogFragment {
	@StringRes private int title;
	@Nullable private String[] extensions;
	private String current_path;
	private ImportListener listener;
	private ArrayList<Folder> items;
	private FolderAdapter adapter = null;
	private FixedHeightRecyclerView recyclerView;

	public ImportDialog() {
	}

	public static ImportDialog newInstance(@StringRes int title, @Nullable String[] extensions, ImportListener listener) {
		ImportDialog dialog = new ImportDialog();
		dialog.title = title;
		dialog.extensions = extensions;
		dialog.listener = listener;
		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().setCanceledOnTouchOutside(true);

		return inflater.inflate(R.layout.dialog_import, container);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			listener.onError(getString(R.string.no_mounted_sd));
			dismiss();
		} else {
			((TextView) view.findViewById(R.id.title_txt)).setText(title);
			current_path = App.last_path != null ? App.last_path : Environment.getExternalStorageDirectory().getAbsolutePath();

			recyclerView = (FixedHeightRecyclerView) view.findViewById(R.id.recyclerView);
			items = new ArrayList<>();
			reload();

			view.findViewById(R.id.positive_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dismiss();
				}
			});
		}
	}

	private void reload() {
		new Thread() {
			@Override
			public void run() {
				items.clear();

				File folder = new File(current_path);
				if (!folder.exists()) folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

				File[] folders = folder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						if (extensions == null) return true;
						if (file.isDirectory()) return true;

						for (String extension : extensions) {
							if (file.getName().endsWith(extension)) return true;
						}

						return false;
					}
				});

				Arrays.sort(folders, new Comparator<File>() {
					@Override
					public int compare(File f1, File f2) {
						if (f1.isDirectory() && !f2.isDirectory()) return -1;
						if (!f1.isDirectory() && f2.isDirectory()) return 1;
						return f1.getName().compareToIgnoreCase(f2.getName());
					}
				});

				if (!folder.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
					File parent = folder.getParentFile();
					items.add(new Folder("../" + parent.getName(), parent.getAbsolutePath(), true));
				}

				for (File file : folders) {
					items.add(new Folder(file.getName(), file.getAbsolutePath(), false, file.isDirectory()));
				}

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						if (adapter == null) {
							adapter = new FolderAdapter(items, new FolderAdapter.ClickListener() {
								@Override
								public void onClick(Folder item) {
									if (item.isDirectory) {
										current_path = item.path;
										reload();
									} else {
										App.last_path = current_path;
										App.instance.putPrefs(App.LAST_PATH_KEY, current_path);
										listener.onSelect(item.path);
										dismiss();
									}
								}
							});

							recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
							recyclerView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});

				interrupt();
			}
		}.start();
	}

	public interface ImportListener {
		void onSelect(String path);
		void onError(String msg);
	}
}
