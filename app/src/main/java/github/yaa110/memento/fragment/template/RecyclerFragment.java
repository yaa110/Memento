package github.yaa110.memento.fragment.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.model.DatabaseModel;

abstract public class RecyclerFragment<T extends DatabaseModel, A extends ModelAdapter> extends Fragment {
	public View fab;
	private RecyclerView recyclerView;
	private View empty;

	private A adapter;
	private ArrayList<T> items;
	private ArrayList<T> selected;
	private Callbacks activity;

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

		Intent data = getActivity().getIntent();
		if (data != null) {
			// TODO get category id for showing notes
		}

		// TODO getItems and display
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.activity = (Callbacks) context;
	}

	public abstract int getLayout();

	public interface Callbacks {
		// TODO
	}
}
