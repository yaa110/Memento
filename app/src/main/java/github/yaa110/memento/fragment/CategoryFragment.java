package github.yaa110.memento.fragment;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.CategoryAdapter;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.model.Category;

public class CategoryFragment extends RecyclerFragment<Category, CategoryAdapter> {
	public CategoryFragment(){}

	@Override
	public int getLayout() {
		return (R.layout.fragment_category);
	}
}
