package github.yaa110.memento.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import github.yaa110.memento.R;
import github.yaa110.memento.adapter.DrawerAdapter;
import github.yaa110.memento.fragment.MainFragment;
import github.yaa110.memento.fragment.template.RecyclerFragment;
import github.yaa110.memento.inner.Animator;
import github.yaa110.memento.inner.Formatter;
import github.yaa110.memento.model.Drawer;

public class MainActivity extends AppCompatActivity implements RecyclerFragment.Callbacks {
	public static final int PERMISSION_REQUEST = 3;

	private DrawerLayout drawerLayout;
	public View drawerHolder;
	private boolean exitStatus = false;

	private MainFragment fragment;
	private Toolbar toolbar;
	private View selectionEdit;
	private boolean permissionNotGranted = false;
	private boolean checkForPermission = true;

	public Handler handler = new Handler();
	public Runnable runnable = new Runnable() {
		@Override
		public void run() {
			exitStatus = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		try {
			//noinspection ConstantConditions
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} catch (Exception ignored) {
		}

		setupDrawer();

		selectionEdit = findViewById(R.id.selection_edit);
		selectionEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fragment.onEditSelected();
			}
		});

		if (savedInstanceState == null) {
			fragment = new MainFragment();

			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment)
				.commit();
		}

		if (checkForPermission) {
			checkForPermission = false;
			if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				new MaterialDialog.Builder(this)
					.title(R.string.permission)
					.content(R.string.storage_permission)
					.positiveText(R.string.request)
					.negativeText(R.string.cancel)
					.negativeColor(ContextCompat.getColor(this, R.color.secondary_text))
					.onPositive(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							dialog.dismiss();
							requestPermission();
						}
					})
					.onNegative(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							dialog.dismiss();
							displayPermissionError();
						}
					})
					.show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(drawerHolder)) {
			drawerLayout.closeDrawers();
			return;
		}

		if (fragment.selectionState) {
			fragment.toggleSelection(false);
			return;
		}

		if (exitStatus) {
			finish();
		} else {
			exitStatus = true;

			try {
				Snackbar.make(fragment.fab != null ? fragment.fab : toolbar, R.string.exit_message, Snackbar.LENGTH_LONG).show();
			} catch (Exception ignored) {}

			handler.postDelayed(runnable, 3500);
		}
	}

	private void setupDrawer() {
		// Set date in drawer
		((TextView) findViewById(R.id.drawer_date)).setText(Formatter.formatDate());

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerHolder = findViewById(R.id.drawer_holder);
		ListView drawerList = (ListView) findViewById(R.id.drawer_list);

		// Navigation menu button
		findViewById(R.id.nav_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				drawerLayout.openDrawer(GravityCompat.START);
			}
		});

		// Settings button
		findViewById(R.id.settings_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickDrawer(Drawer.TYPE_SETTINGS);
			}
		});

		// Set adapter of drawer
		drawerList.setAdapter(new DrawerAdapter(
			getApplicationContext(),
			new DrawerAdapter.ClickListener() {
				@Override
				public void onClick(int type) {
					onClickDrawer(type);
				}
			}
		));
	}

	private void onClickDrawer(final int type) {
		drawerLayout.closeDrawers();

		try {
			handler.removeCallbacks(runnable);
		} catch (Exception ignored) {}

		new Thread() {
			@Override
			public void run() {
				try {
					// wait for completion of drawer animation
					sleep(500);

					switch (type) {
						case Drawer.TYPE_ABOUT:
							// TODO about drawer
							break;
						case Drawer.TYPE_BACKUP:
							// TODO settings drawer
							break;
						case Drawer.TYPE_RESTORE:
							// TODO settings drawer
							break;
						case Drawer.TYPE_SETTINGS:
							// TODO settings drawer
							break;
					}

					interrupt();
				} catch (Exception ignored) {
				}
			}
		}.start();
	}

	@Override
	public void onChangeSelection(boolean state) {
		if (state) {
			Animator.create(getApplicationContext())
				.on(toolbar)
				.setEndVisibility(View.INVISIBLE)
				.animate(R.anim.fade_out);
		} else {
			Animator.create(getApplicationContext())
				.on(toolbar)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fade_in);
		}
	}

	@Override
	public void toggleOneSelection(boolean state) {
		selectionEdit.setVisibility(state ? View.VISIBLE : View.GONE);
	}

	private void requestPermission() {
		ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
	}

	private void displayPermissionError() {
		new MaterialDialog.Builder(this)
			.title(R.string.permission_error)
			.content(R.string.permission_error_desc)
			.negativeText(R.string.request)
			.positiveText(R.string.continue_anyway)
			.negativeColor(ContextCompat.getColor(this, R.color.secondary_text))
			.onPositive(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					dialog.dismiss();
					permissionNotGranted = false;
				}
			})
			.onNegative(new MaterialDialog.SingleButtonCallback() {
				@Override
				public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
					dialog.dismiss();
					requestPermission();
				}
			})
			.show();
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (permissionNotGranted) {
			permissionNotGranted = false;
			displayPermissionError();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(getApplicationContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();;
			} else {
				permissionNotGranted = true;
			}
		}
	}
}
