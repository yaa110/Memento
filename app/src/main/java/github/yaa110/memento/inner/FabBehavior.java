package github.yaa110.memento.inner;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import github.yaa110.memento.widget.Fab;

@SuppressWarnings("unused")
public class FabBehavior extends CoordinatorLayout.Behavior<Fab> {
	public FabBehavior(Context context, AttributeSet attrs) {}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, Fab child, View dependency) {
		return dependency instanceof Snackbar.SnackbarLayout;
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, Fab child, View dependency) {
		if (dependency instanceof Snackbar.SnackbarLayout) {
			float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
			child.setTranslationY(translationY);
			return true;
		}
		return false;
	}

	@Override
	public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, Fab child, View directTargetChild, View target, int nestedScrollAxes) {
		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
			super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
				nestedScrollAxes);
	}

	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, Fab child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

		if (dyConsumed > 0) {
			child.hide();
		} else if (dyConsumed < 0) {
			child.show();
		}
	}
}
