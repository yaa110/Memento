package github.yaa110.memento.widget;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import github.yaa110.memento.R;
import github.yaa110.memento.inner.Formatter;
import github.yaa110.memento.model.Category;
import github.yaa110.memento.widget.template.ModelViewHolder;

public class CategoryViewHolder extends ModelViewHolder<Category> {
	public TextView badge;
	public TextView title;
	public TextView date;
	public TextView counter;

	public CategoryViewHolder(View itemView) {
		super(itemView);
		badge = (TextView) itemView.findViewById(R.id.badge_txt);
		title = (TextView) itemView.findViewById(R.id.title_txt);
		counter = (TextView) itemView.findViewById(R.id.counter_txt);
		date = (TextView) itemView.findViewById(R.id.date_txt);
	}

	@Override
	public void populate(Category item) {
		badge.setText(item.title.substring(0, 1).toUpperCase(Locale.US));
		badge.setBackgroundResource(item.getThemeBackground());
		title.setText(item.title);
		if (item.counter == 0) counter.setText("");
		else if (item.counter == 1) counter.setText(R.string.one_note);
		else counter.setText(String.format(Locale.US, "%d notes", item.counter));
		date.setText(Formatter.formatShortDate(item.createdAt));
	}
}
