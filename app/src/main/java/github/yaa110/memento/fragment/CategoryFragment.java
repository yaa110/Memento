package github.yaa110.memento.fragment;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.Locale;

import github.yaa110.memento.R;
import github.yaa110.memento.activity.NoteActivity;
import github.yaa110.memento.adapter.NoteAdapter;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.db.Controller;
import github.yaa110.memento.db.OpenHelper;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.inner.Animator;
import github.yaa110.memento.model.DatabaseModel;
import github.yaa110.memento.model.Note;

public class CategoryFragment extends RecyclerFragment<Note, NoteAdapter> {
	public View protector;
	public View fab_type;
	public View fab_drawing;
	public boolean isFabOpen = false;

	private ModelAdapter.ClickListener listener = new ModelAdapter.ClickListener() {
		@Override
		public void onClick(DatabaseModel item, int position) {
			startNoteActivity(item.type, item.id, position);
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
	public void init(View view) {
		protector = view.findViewById(R.id.protector);
		fab_type = view.findViewById(R.id.fab_type);
		fab_drawing = view.findViewById(R.id.fab_drawing);

		protector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggleFab(true);
			}
		});

		fab_type.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startNoteActivity(DatabaseModel.TYPE_NOTE_SIMPLE, DatabaseModel.NEW_MODEL_ID, 0);
			}
		});

		fab_drawing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startNoteActivity(DatabaseModel.TYPE_NOTE_DRAWING, DatabaseModel.NEW_MODEL_ID, 0);
			}
		});
	}

	private void startNoteActivity(final int type, final long noteId, final int position) {
		toggleFab(true);

		new Thread() {
			@Override
			public void run() {
				try {
					sleep(150);
				} catch (InterruptedException ignored) {
				}

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(getContext(), NoteActivity.class);
						intent.putExtra(OpenHelper.COLUMN_TYPE, type);
						intent.putExtra("position", position);
						intent.putExtra(OpenHelper.COLUMN_ID, noteId);
						intent.putExtra(OpenHelper.COLUMN_PARENT_ID, categoryId);
						intent.putExtra(OpenHelper.COLUMN_THEME, categoryTheme);
						startActivityForResult(intent, NoteActivity.REQUEST_CODE);
					}
				});

				interrupt();
			}
		}.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if (requestCode == NoteActivity.REQUEST_CODE) {
			final int position = data.getIntExtra("position", 0);

			switch (resultCode) {
				case NoteActivity.RESULT_NEW:
					Note note = new Note();
					note.title = data.getStringExtra(OpenHelper.COLUMN_TITLE);
					note.type = data.getIntExtra(OpenHelper.COLUMN_TYPE, DatabaseModel.TYPE_NOTE_SIMPLE);
					note.createdAt = data.getLongExtra(OpenHelper.COLUMN_DATE, System.currentTimeMillis());
					note.id = data.getLongExtra(OpenHelper.COLUMN_ID, DatabaseModel.NEW_MODEL_ID);
					addItem(note, position);
					break;
				case NoteActivity.RESULT_EDIT:
					Note item = items.get(position);
					item.title = data.getStringExtra(OpenHelper.COLUMN_TITLE);
					refreshItem(position);
					break;
				case NoteActivity.RESULT_DELETE:
					new Thread() {
						@Override
						public void run() {
							Controller.instance.deleteNotes(
								new String[] {
									String.format(Locale.US, "%d", data.getLongExtra(OpenHelper.COLUMN_ID, DatabaseModel.NEW_MODEL_ID))
								},
								categoryId
							);

							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									final Note deletedItem = deleteItem(position);
									Snackbar.make(fab != null ? fab : selectionToolbar, "1 note was deleted", 7000)
										.setAction(R.string.undo, new View.OnClickListener() {
											@Override
											public void onClick(View view) {
												new Thread() {
													@Override
													public void run() {
														Controller.instance.undoDeletion();
														Controller.instance.addCategoryCounter(deletedItem.categoryId, 1);

														getActivity().runOnUiThread(new Runnable() {
															@Override
															public void run() {
																addItem(deletedItem, position);
															}
														});

														interrupt();
													}
												}.start();
											}
										})
										.show();
								}
							});

							interrupt();
						}
					}.start();
					break;
			}
		}
	}

	@Override
	public void onClickFab() {
		toggleFab(false);
	}

	public void toggleFab(boolean forceClose) {
		if (isFabOpen) {
			isFabOpen = false;

			Animator.create(getContext())
				.on(protector)
				.setEndVisibility(View.GONE)
				.animate(R.anim.fade_out);

			Animator.create(getContext())
				.on(fab)
				.animate(R.anim.fab_rotate_back);

			Animator.create(getContext())
				.on(fab_type)
				.setEndVisibility(View.GONE)
				.animate(R.anim.fab_out);

			Animator.create(getContext())
				.on(fab_drawing)
				.setDelay(50)
				.setEndVisibility(View.GONE)
				.animate(R.anim.fab_out);
		} else if (!forceClose) {
			isFabOpen = true;

			Animator.create(getContext())
				.on(protector)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fade_in);

			Animator.create(getContext())
				.on(fab)
				.animate(R.anim.fab_rotate);

			Animator.create(getContext())
				.on(fab_type)
				.setDelay(80)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fab_in);

			Animator.create(getContext())
				.on(fab_drawing)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fab_in);
		}
	}

	@Override
	public int getLayout() {
		return R.layout.fragment_category;
	}

	@Override
	public String getItemName() {
		return "note";
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
