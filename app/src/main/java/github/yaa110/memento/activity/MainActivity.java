package github.yaa110.memento.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import github.yaa110.memento.App;
import github.yaa110.memento.R;
import github.yaa110.memento.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		try {
			//noinspection ConstantConditions
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} catch (Exception ignored) {
		}

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new MainFragment())
				.commit();
		}
	}
}
