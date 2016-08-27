package github.yaa110.memento.dialog;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

import github.yaa110.memento.App;
import github.yaa110.memento.R;
import github.yaa110.memento.adapter.FolderAdapter;
import github.yaa110.memento.model.Folder;
import github.yaa110.memento.widget.FixedHeightRecyclerView;

public class SaveDialog extends DialogFragment {
	@StringRes
	private int title;
	private String filename_prefix;
	private String extension;
	private String current_path;
	private SaveListener listener;
	private ArrayList<Folder> items;
	private FolderAdapter adapter = null;
	private FixedHeightRecyclerView recyclerView;
	private boolean isWorking = false;
	private boolean canceled = true;

	public static SaveDialog newInstance(@StringRes int title, String filename_prefix, String extension, SaveListener listener) {
		SaveDialog dialog = new SaveDialog();
		dialog.title = title;
		dialog.filename_prefix = filename_prefix;
		dialog.extension = extension;
		dialog.listener = listener;
		return dialog;
	}

	public SaveDialog() {
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().setCanceledOnTouchOutside(true);

		return inflater.inflate(R.layout.dialog_save, container);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private boolean isDirWritable() throws Exception {
		File temp_file = new File(current_path, "temp.tmp");
		if (temp_file.exists()) temp_file.delete();
		if (temp_file.createNewFile()) {
			temp_file.delete();
			return true;
		}
		return false;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			new Thread() {
				@Override
				public void run() {
					isWorking = true;
					current_path = getContext().getFilesDir().getAbsolutePath();
					try {
						if (isDirWritable()) pathSelected();
					} catch (Exception ignored) {
						listener.onError();
					} finally {
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								dismiss();
							}
						});
						interrupt();
					}
				}
			}.start();
		} else {
			((TextView) view.findViewById(R.id.title_txt)).setText(getString(title));
			current_path = App.last_path != null ? App.last_path : Environment.getExternalStorageDirectory().getAbsolutePath();

			recyclerView = (FixedHeightRecyclerView) view.findViewById(R.id.recyclerView);
			items = new ArrayList<>();
			reload();

			view.findViewById(R.id.positive_btn).setOnClickListener(new View.OnClickListener() {
				@SuppressWarnings("ResultOfMethodCallIgnored")
				@Override
				public void onClick(View view) {
					if (isWorking) return;
					isWorking = true;
					new Thread() {
						@SuppressWarnings("ConstantConditions")
						@Override
						public void run() {
							try {
								try {
									if (isDirWritable()) {
										App.last_path = current_path;
										App.instance.putPrefs(App.LAST_PATH_KEY, current_path);
										pathSelected();
									}
								} catch (Exception ignored) {
									try {
										current_path = getContext().getExternalFilesDir(null).getAbsolutePath();
										if (isDirWritable()) pathSelected();
									} catch (Exception ignored2) {
										current_path = getContext().getFilesDir().getAbsolutePath();
										try {
											if (isDirWritable()) pathSelected();
										} catch (Exception e) {
											listener.onError();
										}
									}
								}
							} finally {
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										canceled = false;
										dismiss();
									}
								});
								interrupt();
							}
						}
					}.start();
				}
			});

			view.findViewById(R.id.negative_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isWorking) return;
					canceled = true;
					dismiss();
				}
			});

			view.findViewById(R.id.new_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ContentDialog.newInstance(
						R.string.new_folder,
						R.string.create,
						R.string.cancel,
						-1,
						R.layout.dialog_new_folder,
						new ContentDialog.DialogListener() {
							private EditText name_txt;
							private boolean isCreating = false;

							@Override
							public void onPositive(ContentDialog dialog, View content) {
								if (isCreating || !dialog.checkEditText(name_txt)) return;
								isCreating = true;

								try {
									String folder_name = name_txt.getText().toString();
									File folder = new File(current_path, folder_name);

									int counter = 2;
									while (folder.exists()) {
										folder = new File(current_path, String.format(Locale.US, "%s(%d)", folder_name, counter));
										counter++;
									}

									//noinspection ResultOfMethodCallIgnored
									folder.mkdirs();
									reload();
								} catch (Exception ignored) {
								} finally {
									dialog.dismiss();
								}
							}

							@Override
							public void onNegative(ContentDialog dialog, View content) {
								if (isCreating) return;
								dialog.dismiss();
							}

							@Override
							public void onNeutral(ContentDialog dialog, View content) {
							}

							@Override
							public void onInit(View content) {
								name_txt = (EditText) content.findViewById(R.id.name_txt);
							}
						}
					).show(getFragmentManager(), "");
				}
			});
		}
	}

	private void pathSelected() {
		Calendar calendar = Calendar.getInstance(Locale.US);
		filename_prefix = String.format(Locale.US, "%s-%d-%02d-%02d", filename_prefix, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

		File save_path = new File(current_path, String.format("%s.%s", filename_prefix, extension));

		int i = 2;
		while (save_path.exists()) {
			save_path = new File(current_path, String.format(Locale.US, "%s(%d).%s", filename_prefix, i, extension));
			i++;
		}

		listener.onSelect(save_path.getAbsolutePath());
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
						return file.isDirectory();
					}
				});

				Arrays.sort(folders, new Comparator<File>() {
					@Override
					public int compare(File f1, File f2) {
						return f1.getName().compareToIgnoreCase(f2.getName());
					}
				});

				if (!folder.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
					File parent = folder.getParentFile();
					items.add(new Folder("../" + parent.getName(), parent.getAbsolutePath(), true));
				}

				for (File file : folders) {
					items.add(new Folder(file.getName(), file.getAbsolutePath(), false));
				}

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						if (adapter == null) {
							adapter = new FolderAdapter(items, new FolderAdapter.ClickListener() {
								@Override
								public void onClick(Folder item) {
									if (isWorking) return;
									current_path = item.path;
									reload();
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

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (canceled) listener.onCancel();
		super.onDismiss(dialog);
	}

	public interface SaveListener {
		void onSelect(String path);
		void onError();
		void onCancel();
	}
}
