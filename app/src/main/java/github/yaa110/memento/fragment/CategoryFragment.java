package github.yaa110.memento.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.CategoryAdapter;
import github.yaa110.memento.adapter.template.ModelAdapter;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.model.DatabaseModel;

public class CategoryFragment extends RecyclerFragment<Category, CategoryAdapter> {
	private int categoryDialogTheme = Category.THEME_GREEN;
	private boolean categoryDialogIsEditing = false;

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
		categoryDialogTheme = Category.THEME_GREEN;
		categoryDialogIsEditing = false;
		displayCategoryDialog(R.string.new_category, R.string.create, "");
	}

	private void displayCategoryDialog(@StringRes int title, @StringRes int positiveText, String categoryTitle) {
		MaterialDialog dialog = new MaterialDialog.Builder(getContext())
			.title(title)
			.positiveText(positiveText)
			.negativeText(R.string.cancel)
			.negativeColor(ContextCompat.getColor(getContext(), R.color.secondary_text))
			.onPositive(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					// TODO
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
