package github.yaa110.memento.adapter.template;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.widget.template.ModelViewHolder;

abstract public class ModelAdapter<T extends DatabaseModel, VH extends ModelViewHolder<T>> extends RecyclerView.Adapter<VH> {
	private ArrayList<T> items;
	private ArrayList<T> selected;
	private ClickListener<T> listener;

	public ModelAdapter(ArrayList<T> items, ArrayList<T> selected, ClickListener<T> listener) {
		this.items = items;
		this.selected = selected;
		this.listener = listener;
	}

	@Override
	public void onBindViewHolder(final VH holder, int position) {
		final T item = items.get(position);

		// Populate view
		holder.populate(item);

		// Check if item is selected
		if (selected.contains(item)) holder.setSelected(true);
		else holder.setSelected(false);

		holder.holder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (selected.isEmpty()) listener.onClick(item, items.indexOf(item));
				else toggleSelection(holder, item);
			}
		});

		holder.holder.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				toggleSelection(holder, item);
				return true;
			}
		});
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	private void toggleSelection(VH holder, T item) {
		if (selected.contains(item)) {
			selected.remove(item);
			holder.setSelected(false);
			if (selected.isEmpty()) listener.onChangeSelection(false);
		} else {
			if (selected.isEmpty()) listener.onChangeSelection(true);
			selected.add(item);
			holder.setSelected(true);
		}
		listener.onCountSelection(selected.size());
	}

	public interface ClickListener<M extends DatabaseModel> {
		void onClick(M item, int position);
		void onChangeSelection(boolean haveSelected);
		void onCountSelection(int count);
	}
}
