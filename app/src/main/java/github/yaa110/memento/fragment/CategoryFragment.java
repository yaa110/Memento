package github.yaa110.memento.fragment;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.NoteAdapter;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.model.Note;

public class CategoryFragment extends RecyclerFragment<Note, NoteAdapter> {
	private ModelAdapter.ClickListener listener = new ModelAdapter.ClickListener() {
		@Override
		public void onClick(DatabaseModel item, int position) {
			// TODO
		}

		@Override
		public void onChangeSelection(boolean haveSelected) {
			toggleSelection(haveSelected);
		}

		@Override
		public void onCountSelection(int count) {
			onChangeCounter(count);
		}
	};

	public CategoryFragment() {}

	@Override
	public void onClickFab() {
		// TODO new note
	}

	@Override
	public int getLayout() {
		return R.layout.fragment_category;
	}

	@Override
	public Class<NoteAdapter> getAdapterClass() {
		return NoteAdapter.class;
	}

	@Override
	public ModelAdapter.ClickListener getListener() {
		return listener;
	}
}
