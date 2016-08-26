package github.yaa110.memento.widget;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import github.yaa110.memento.R;
import github.yaa110.memento.inner.Formatter;
import github.yaa110.memento.model.Note;
import github.yaa110.memento.widget.template.ModelViewHolder;

public class NoteViewHolder extends ModelViewHolder<Note> {
	public TextView badge;
	public TextView title;
	public TextView date;

	public NoteViewHolder(View itemView) {
		super(itemView);
		badge = (TextView) itemView.findViewById(R.id.badge_txt);
		title = (TextView) itemView.findViewById(R.id.title_txt);
		date = (TextView) itemView.findViewById(R.id.date_txt);
	}

	@Override
	public void populate(Note item) {
		badge.setText(item.title.substring(0, 1).toUpperCase(Locale.US));
		badge.setBackgroundResource(item.getThemeBackground());
		title.setText(item.title);
		date.setText(Formatter.formatShortDate(item.createdAt));
	}
}
