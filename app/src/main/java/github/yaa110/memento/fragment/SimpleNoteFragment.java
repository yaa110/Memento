package github.yaa110.memento.fragment;

import android.view.View;

import github.yaa110.memento.R;
import github.yaa110.memento.fragment.template.NoteFragment;
import github.yaa110.memento.model.DatabaseModel;
import in.nashapp.androidsummernote.Summernote;

public class SimpleNoteFragment extends NoteFragment {
	private Summernote body;

	public SimpleNoteFragment() {}

	@Override
	public int getLayout() {
		return R.layout.fragment_simple_note;
	}

	@Override
	public void saveNote(final SaveListener listener) {
		super.saveNote(listener);
		note.body = body.getText();

		new Thread() {
			@Override
			public void run() {
				long id = note.save();
				if (note.id == DatabaseModel.NEW_MODEL_ID) {
					note.id = id;
				}
				listener.onSave();
				interrupt();
			}
		}.start();
	}

	@Override
	public void init(View view) {
		body = (Summernote) view.findViewById(R.id.wysiwyg_txt);
		body.setText(note.body);
	}
}
