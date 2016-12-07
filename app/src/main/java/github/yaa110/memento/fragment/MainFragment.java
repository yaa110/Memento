package github.yaa110.memento.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import github.yaa110.memento.R;
import github.yaa110.memento.activity.CategoryActivity;
import github.yaa110.memento.adapter.CategoryAdapter;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.db.OpenHelper;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.model.DatabaseModel;

public class MainFragment extends RecyclerFragment<Category, CategoryAdapter> {
	private int categoryDialogTheme = Category.THEME_GREEN;

	private ModelAdapter.ClickListener listener = new ModelAdapter.ClickListener() {
		@Override
		public void onClick(DatabaseModel item, int position) {
			Intent intent = new Intent(getContext(), CategoryActivity.class);
			intent.putExtra("position", position);
			intent.putExtra(OpenHelper.COLUMN_ID, item.id);
			intent.putExtra(OpenHelper.COLUMN_TITLE, item.title);
			intent.putExtra(OpenHelper.COLUMN_THEME, ((Category) item).theme);
			startActivityForResult(intent, CategoryActivity.REQUEST_CODE);
		}

		@Override
		public void onChangeSelection(boolean haveSelected) {
			toggleSelection(haveSelected);
		}

		@Override
		public void onCountSelection(int count) {
			onChangeCounter(count);
			activity.toggleOneSelection(count <= 1);
		}
	};

	public MainFragment(){}

	@Override
	public void onClickFab() {
		categoryDialogTheme = Category.THEME_GREEN;
		displayCategoryDialog(
			R.string.new_category,
			R.string.create,
			"",
			DatabaseModel.NEW_MODEL_ID,
			0
		);
	}

	public void onEditSelected() {
		if (!selected.isEmpty()) {
			Category item = selected.remove(0);
			int position = items.indexOf(item);
			refreshItem(position);
			toggleSelection(false);
			categoryDialogTheme = item.theme;
			displayCategoryDialog(
				R.string.edit_category,
				R.string.edit,
				item.title,
				item.id,
				position
			);
		}
	}

	private void displayCategoryDialog(@StringRes int title, @StringRes int positiveText, final String categoryTitle, final long categoryId, final int position) {
		MaterialDialog dialog = new MaterialDialog.Builder(getContext())
			.title(title)
			.positiveText(positiveText)
			.negativeText(R.string.cancel)
			.negativeColor(ContextCompat.getColor(getContext(), R.color.secondary_text))
			.onPositive(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					//noinspection ConstantConditions
					String inputTitle = ((EditText) dialog.getCustomView().findViewById(R.id.title_txt)).getText().toString();
					if (inputTitle.isEmpty()) {
						inputTitle = "Untitled";
					}

					final Category category = new Category();
					category.id = categoryId;

					final boolean isEditing = categoryId != DatabaseModel.NEW_MODEL_ID;

					if (!isEditing) {
						category.counter = 0;
						category.type = DatabaseModel.TYPE_CATEGORY;
						category.createdAt = System.currentTimeMillis();
						category.isArchived = false;
					}

					category.title = inputTitle;
					category.theme = categoryDialogTheme;

					new Thread() {
						@Override
						public void run() {
							final long id = category.save();
							if (id != DatabaseModel.NEW_MODEL_ID) {
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (isEditing) {
											Category categoryInItems = items.get(position);
											categoryInItems.theme = category.theme;
											categoryInItems.title = category.title;
											refreshItem(position);
										} else {
											category.id = id;
											addItem(category, position);
										}
									}
								});
							}

							interrupt();
						}
					}.start();

				}
			})
			.onNegative(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					dialog.dismiss();
				}
			})
			.customView(R.layout.dialog_category, true)
			.build();

		dialog.show();

		final View view = dialog.getCustomView();

		//noinspection ConstantConditions
		((EditText) view.findViewById(R.id.title_txt)).setText(categoryTitle);
		setCategoryDialogTheme(view, categoryDialogTheme);

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_red).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_RED);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_pink).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_PINK);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_purple).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_PURPLE);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_amber).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_AMBER);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_blue).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_BLUE);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_cyan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_CYAN);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_orange).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_ORANGE);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_teal).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_TEAL);
			}
		});

		//noinspection ConstantConditions
		view.findViewById(R.id.theme_green).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCategoryDialogTheme(view, Category.THEME_GREEN);
			}
		});
	}

	private void setCategoryDialogTheme(View view, int theme) {
		if (theme != categoryDialogTheme) {
			getThemeView(view, categoryDialogTheme).setImageResource(0);
		}

		getThemeView(view, theme).setImageResource(R.drawable.ic_checked);
		categoryDialogTheme = theme;
	}

	private ImageView getThemeView(View view, int theme) {
		switch (theme) {
			case Category.THEME_AMBER:
				return (ImageView) view.findViewById(R.id.theme_amber);
			case Category.THEME_BLUE:
				return (ImageView) view.findViewById(R.id.theme_blue);
			case Category.THEME_CYAN:
				return (ImageView) view.findViewById(R.id.theme_cyan);
			case Category.THEME_ORANGE:
				return (ImageView) view.findViewById(R.id.theme_orange);
			case Category.THEME_PINK:
				return (ImageView) view.findViewById(R.id.theme_pink);
			case Category.THEME_PURPLE:
				return (ImageView) view.findViewById(R.id.theme_purple);
			case Category.THEME_RED:
				return (ImageView) view.findViewById(R.id.theme_red);
			case Category.THEME_TEAL:
				return (ImageView) view.findViewById(R.id.theme_teal);
			default:
				return (ImageView) view.findViewById(R.id.theme_green);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CategoryActivity.REQUEST_CODE && resultCode == CategoryActivity.RESULT_CHANGE) {
			int position = data.getIntExtra("position", 0);
			items.get(position).counter = data.getIntExtra(OpenHelper.COLUMN_COUNTER, 0);
			refreshItem(position);
		}
	}

	@Override
	public int getLayout() {
		return (R.layout.fragment_main);
	}

	@Override
	public String getItemName() {
		return "category";
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
