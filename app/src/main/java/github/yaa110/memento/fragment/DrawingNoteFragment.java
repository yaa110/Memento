package github.yaa110.memento.fragment;

import android.view.View;

import github.yaa110.memento.R;
import github.yaa110.memento.fragment.template.NoteFragment;

public class DrawingNoteFragment extends NoteFragment {

	public DrawingNoteFragment() {}

	@Override
	public int getLayout() {
		return R.layout.fragment_drawing_note;
	}

	@Override
	public void saveNote(SaveListener listener) {
		super.saveNote(listener);
		// TODO populate and save note object of NoteFragment
	}

	@Override
	public void init(View view) {
		// TODO
	}
}
