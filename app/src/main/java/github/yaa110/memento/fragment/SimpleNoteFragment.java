package github.yaa110.memento.fragment;

import android.view.View;

import github.yaa110.memento.R;
import github.yaa110.memento.fragment.template.NoteFragment;

public class SimpleNoteFragment extends NoteFragment {

	public SimpleNoteFragment() {}

	@Override
	public int getLayout() {
		return R.layout.fragment_simple_note;
	}

	@Override
	public void init(View view) {

	}
}
