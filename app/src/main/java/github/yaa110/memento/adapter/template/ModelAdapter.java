package github.yaa110.memento.adapter.template;

import android.support.v7.widget.RecyclerView;

import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.widget.template.ModelViewHolder;

abstract public class ModelAdapter<T extends DatabaseModel, VH extends ModelViewHolder<T>> extends RecyclerView.Adapter<VH> {

}
