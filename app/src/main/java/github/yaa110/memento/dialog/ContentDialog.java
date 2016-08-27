package github.yaa110.memento.dialog;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import github.yaa110.memento.R;
import github.yaa110.memento.inner.Animator;
import github.yaa110.memento.widget.MaxHeightScrollView;

public class ContentDialog extends DialogFragment {
	private View content;
	private DialogListener listener;
	private boolean isWorking = false;
	private View loading;

	public ContentDialog() {}

	public static ContentDialog newInstance(@StringRes int title, @StringRes int positive, @StringRes int negative, @StringRes int neutral, @LayoutRes int layoutRes, DialogListener listener) {
		ContentDialog dialog = new ContentDialog();
		dialog.listener = listener;
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("positive", positive);
		args.putInt("negative", negative);
		args.putInt("neutral", neutral);
		args.putInt("layoutRes", layoutRes);
		dialog.setArguments(args);
		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().setCanceledOnTouchOutside(true);

		View view = inflater.inflate(R.layout.dialog_content, container);
		content = inflater.inflate(getArguments().getInt("layoutRes"), (MaxHeightScrollView) view.findViewById(R.id.content_holder));
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle args = getArguments();

		((TextView) view.findViewById(R.id.title_txt)).setText(args.getInt("title"));

		int positive = args.getInt("positive");
		int negative = args.getInt("negative");
		int neutral = args.getInt("neutral");

		loading = view.findViewById(R.id.loading);

		final ContentDialog dialog = this;

		TextView positive_btn = (TextView) view.findViewById(R.id.positive_btn);
		if (positive != -1) {
			positive_btn.setVisibility(View.VISIBLE);
			positive_btn.setText(positive);
			positive_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isWorking) return;
					listener.onPositive(dialog, content);
				}
			});
		} else {
			positive_btn.setVisibility(View.GONE);
		}

		TextView negative_btn = (TextView) view.findViewById(R.id.negative_btn);
		if (negative != -1) {
			negative_btn.setVisibility(View.VISIBLE);
			negative_btn.setText(negative);
			negative_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isWorking) return;
					listener.onNegative(dialog, content);
				}
			});
		} else {
			negative_btn.setVisibility(View.GONE);
		}

		TextView neutral_btn = (TextView) view.findViewById(R.id.neutral_btn);
		if (neutral != -1) {
			neutral_btn.setVisibility(View.VISIBLE);
			neutral_btn.setText(neutral);
			neutral_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isWorking) return;
					listener.onNeutral(dialog, content);
				}
			});
		} else {
			neutral_btn.setVisibility(View.GONE);
		}

		listener.onInit(content);
	}

	public void setWorking(boolean status) {
		isWorking = status;
		loading.setVisibility(status ? View.VISIBLE : View.GONE);
	}

	public void displayError(EditText target, int text) {
		target.setError(getString(text));

		Animator.create(getContext().getApplicationContext())
			.on(getView())
			.animate(R.anim.shake);
	}

	public boolean checkEditText(EditText target) {
		if (target.length() > 0) return true;

		target.setError(getString(R.string.required));

		Animator.create(getContext().getApplicationContext())
			.on(getView())
			.animate(R.anim.shake);

		return false;
	}

	public interface DialogListener {
		void onPositive(ContentDialog dialog, View content);
		void onNegative(ContentDialog dialog, View content);
		void onNeutral(ContentDialog dialog, View content);
		void onInit(View content);
	}
}
