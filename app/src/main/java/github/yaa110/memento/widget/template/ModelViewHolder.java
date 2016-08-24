package github.yaa110.memento.widget.template;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import github.yaa110.memento.model.DatabaseModel;

abstract public class ModelViewHolder<T extends DatabaseModel> extends RecyclerView.ViewHolder {
	public View holder;

	public ModelViewHolder(View itemView) {
		super(itemView);
	}

	abstract public void setSelected(T item, boolean status);
	abstract public void populate(T item);
}
