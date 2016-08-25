package github.yaa110.memento.fragment;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.CategoryAdapter;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.model.DatabaseModel;

public class CategoryFragment extends RecyclerFragment<Category, CategoryAdapter> {
	private ModelAdapter.ClickListener listener = new ModelAdapter.ClickListener() {
		@Override
		public void onClick(DatabaseModel item, int position) {
			// TODO onChangeSelection
		}

		@Override
		public void onChangeSelection(boolean haveSelected) {
			// TODO onChangeSelection
		}
	};

	public CategoryFragment(){}

	@Override
	public void onClickFab() {
		// TODO
	}

	@Override
	public int getLayout() {
		return (R.layout.fragment_category);
	}

	@Override
	public Class<CategoryAdapter> getAdapterClass() {
		return CategoryAdapter.class;
	}

	@Override
	public ModelAdapter.ClickListener getListener() {
		return listener;
	}
}
