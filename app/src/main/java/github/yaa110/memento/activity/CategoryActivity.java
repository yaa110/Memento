package github.yaa110.memento.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import github.yaa110.memento.R;
import github.yaa110.memento.db.OpenHelper;
import github.yaa110.memento.model.Category;

public class CategoryActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setTheme(Category.getStyle(getIntent().getIntExtra(OpenHelper.COLUMN_THEME, Category.THEME_GREEN)));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
	}
}
