package github.yaa110.memento.inner;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

@SuppressWarnings("unused")
public class Animator {

	private Context context;
	private View view;
	private boolean clear = true;
	private long delay = 0;
	private int start_visiblity = View.VISIBLE;
	private int end_visiblity = View.VISIBLE;
	private AnimatorListener listener = null;

	private Animator(Context context) {
		this.context = context;
	}

	public static Animator create(Context context) {
		return new Animator(context);
	}

	public <T extends View> Animator on(T view) {
		this.view = view;
		return this;
	}

	public Animator setDelay(long delay) {
		this.delay = delay;
		return this;
	}

	public Animator setClear(boolean clear) {
		this.clear = clear;
		return this;
	}

	public Animator setStartVisibility(int visiblity) {
		this.start_visiblity = visiblity;
		return this;
	}

	public Animator setEndVisibility(int visiblity) {
		this.end_visiblity = visiblity;
		return this;
	}

	public Animator setListener(AnimatorListener listener) {
		this.listener = listener;
		return this;
	}

	public void animate(int anim_id) {
		Animation animation = AnimationUtils.loadAnimation(context, anim_id);
		if (delay > 0) {
			animation.setStartOffset(delay);
		}
		animation.setAnimationListener(new Animation.AnimationListener() {
			boolean end_status = false;

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!end_status) {
					end_status = true;
					if (clear) view.clearAnimation();
					view.setVisibility(end_visiblity);
					if (listener != null) {
						listener.onAnimate();
					}
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		view.setVisibility(start_visiblity);
		view.startAnimation(animation);
	}

	public interface AnimatorListener {
		void onAnimate();
	}

}
