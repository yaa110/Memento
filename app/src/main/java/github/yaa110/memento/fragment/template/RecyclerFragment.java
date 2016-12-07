package github.yaa110.memento.fragment.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.db.Controller;
import github.yaa110.memento.db.OpenHelper;
import github.yaa110.memento.inner.Animator;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.model.Note;

abstract public class RecyclerFragment<T extends DatabaseModel, A extends ModelAdapter> extends Fragment {
	public View fab;
	private RecyclerView recyclerView;
	private View empty;
	public Toolbar selectionToolbar;
	private TextView selectionCounter;
	public boolean selectionState = false;

	private A adapter;
	public ArrayList<T> items;
	public ArrayList<T> selected = new ArrayList<>();
	public Callbacks activity;

	public long categoryId = DatabaseModel.NEW_MODEL_ID;
	public String categoryTitle;
	public int categoryTheme;
	public int categoryPosition = 0;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(getLayout(), container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		fab = view.findViewById(R.id.fab);
		recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		empty = view.findViewById(R.id.empty);
		selectionToolbar = (Toolbar) getActivity().findViewById(R.id.selection_toolbar);
		selectionCounter = (TextView) selectionToolbar.findViewById(R.id.selection_counter);

		init(view);

		selectionToolbar.findViewById(R.id.selection_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggleSelection(false);
			}
		});

		selectionToolbar.findViewById(R.id.selection_delete).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final ArrayList<T> undos = new ArrayList<>();
				undos.addAll(selected);
				toggleSelection(false);

				new Thread() {
					@Override
					public void run() {
						final int length = undos.size();
						String[] ids = new String[length];
						final int[] sortablePosition = new int[length];

						for (int i = 0; i < length; i++) {
							T item = undos.get(i);
							ids[i] = String.format(Locale.US, "%d", item.id);
							int position = items.indexOf(item);
							item.position = position;
							sortablePosition[i] = position;
						}

						Controller.instance.deleteNotes(ids, categoryId);

						Arrays.sort(sortablePosition);

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								for (int i = length - 1; i >= 0; i--) {
									items.remove(sortablePosition[i]);
									adapter.notifyItemRemoved(sortablePosition[i]);
								}

								toggleEmpty();

								StringBuilder message = new StringBuilder();
								message.append(length).append(" ").append(getItemName());
								if (length > 1) message.append("s were deleted");
								else message.append(" was deleted.");

								Snackbar.make(fab != null ? fab : selectionToolbar, message.toString(), 7000)
									.setAction(R.string.undo, new View.OnClickListener() {
										@Override
										public void onClick(View view) {
											new Thread() {
												@Override
												public void run() {
													Controller.instance.undoDeletion();
													if (categoryId != DatabaseModel.NEW_MODEL_ID) {
														Controller.instance.addCategoryCounter(categoryId, length);
													}

													Collections.sort(undos, new Comparator<T>() {
														@Override
														public int compare(T t1, T t2) {
															if (t1.position < t2.position) return -1;
															if (t1.position == t2.position) return 0;
															return 1;
														}
													});

													getActivity().runOnUiThread(new Runnable() {
														@Override
														public void run() {
															for (int i = 0; i < length; i++) {
																T item = undos.get(i);
																addItem(item, item.position);
															}
														}
													});
													interrupt();
												}
											}.start();
										}
									})
									.show();
							}
						});

						interrupt();
					}
				}.start();
			}
		});

		if (fab != null) {
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickFab();
				}
			});
		}

		Intent data = getActivity().getIntent();
		if (data != null) {
			// Get the parent data
			categoryId = data.getLongExtra(OpenHelper.COLUMN_ID, DatabaseModel.NEW_MODEL_ID);
			categoryTitle = data.getStringExtra(OpenHelper.COLUMN_TITLE);
			categoryTheme = data.getIntExtra(OpenHelper.COLUMN_THEME, Category.THEME_GREEN);
			categoryPosition = data.getIntExtra("position", 0);

			if (categoryTitle != null) {
				((TextView) getActivity().findViewById(R.id.title)).setText(categoryTitle);
			}
		}

		loadItems();
	}

	public void onChangeCounter(int count) {
		selectionCounter.setText(String.format(Locale.US, "%d", count));
	}

	public void toggleSelection(boolean state) {
		selectionState = state;
		activity.onChangeSelection(state);
		if (state) {
			Animator.create(getContext())
				.on(selectionToolbar)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fade_in);
		} else {
			Animator.create(getContext())
				.on(selectionToolbar)
				.setEndVisibility(View.GONE)
				.animate(R.anim.fade_out);

			deselectAll();
		}
	}

	private void deselectAll() {
		while (!selected.isEmpty()) {
			adapter.notifyItemChanged(items.indexOf(selected.remove(0)));
		}
	}

	public void loadItems() {
		new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					if (categoryId == DatabaseModel.NEW_MODEL_ID) {
						// Get all categories
						items = (ArrayList<T>) Category.all();
					} else {
						// Get notes of the category by categoryId
						items = (ArrayList<T>) Note.all(categoryId);
					}

					adapter = getAdapterClass().getDeclaredConstructor(
						ArrayList.class,
						ArrayList.class,
						ModelAdapter.ClickListener.class
					).newInstance(items, selected, getListener());

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							toggleEmpty();

							recyclerView.setAdapter(adapter);
							recyclerView.setLayoutManager(new LinearLayoutManager(
								getContext(),
								LinearLayoutManager.VERTICAL,
								false
							));
						}
					});
				} catch (Exception ignored) {
				} finally {
					interrupt();
				}
			}
		}.start();
	}

	private void toggleEmpty() {
		if (items.isEmpty()) {
			empty.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);
		} else {
			empty.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
		}
	}

	public void refreshItem(int position) {
		adapter.notifyItemChanged(position);
	}

	public T deleteItem(int position) {
		T item = items.remove(position);
		adapter.notifyItemRemoved(position);
		toggleEmpty();
		return item;
	}

	public void addItem(T item, int position) {
		if (items.isEmpty()) {
			empty.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
		}

		items.add(position, item);
		adapter.notifyItemInserted(position);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.activity = (Callbacks) context;
	}

	public void init(View view) {}

	public abstract void onClickFab();
	public abstract int getLayout();
	public abstract String getItemName();
	public abstract Class<A> getAdapterClass();
	public abstract ModelAdapter.ClickListener getListener();

	public interface Callbacks {
		void onChangeSelection(boolean state);
		void toggleOneSelection(boolean state);
	}
}
