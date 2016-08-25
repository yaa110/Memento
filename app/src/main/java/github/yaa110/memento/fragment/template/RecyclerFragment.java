package github.yaa110.memento.fragment.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.db.OpenHelper;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.model.Note;

abstract public class RecyclerFragment<T extends DatabaseModel, A extends ModelAdapter> extends Fragment {
	public View fab;
	private RecyclerView recyclerView;
	private View empty;

	private A adapter;
	public ArrayList<T> items;
	public ArrayList<T> selected;
	public Callbacks activity;

	public long categoryId = DatabaseModel.NEW_MODEL_ID;
	public String categoryTitle;

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

			if (categoryTitle != null) {
				((TextView) getActivity().findViewById(R.id.title)).setText(categoryTitle);
			}
		}

		loadItems();
	}

	private void loadItems() {
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

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.activity = (Callbacks) context;
	}

	public abstract void onClickFab();
	public abstract int getLayout();
	public abstract Class<A> getAdapterClass();
	public abstract ModelAdapter.ClickListener getListener();

	public interface Callbacks {
		// TODO
	}
}
