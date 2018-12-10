package flamenco.flamenco;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.animation.LayoutTransition;
import android.app.Activity;

public class animations {

    public static void hideViewDown(final View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_down);
        animation.setFillEnabled(false);
        animation.setDuration(100);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();

            }
        });
        view.startAnimation(animation);
    }

    public static void showViewDown(final View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_down);
        animation.setDuration(100);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();

            }
        });
        view.startAnimation(animation);

    }


    public static void hideViewUp(final View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_up);
        animation.setFillEnabled(false);
        animation.setDuration(100);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();

            }
        });
        view.startAnimation(animation);
    }

    public static void showViewUp(final View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);
        animation.setDuration(100);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();

            }
        });
        view.startAnimation(animation);

    }
    public static void expandObject(final View view, float x, float y) {
        int time = 200;
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", 1f, x);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", 1f, y);
        animationX.setDuration(time);
        animationX.setInterpolator(new AccelerateInterpolator(2));
        animationY.setDuration(time);
        animationY.setInterpolator(new AccelerateInterpolator(2));
        animationX.start();
        animationY.start();
    }
    public static void contractObject(final View view, float x, float y) {
        int time = 200;
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", x, 1f);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", y, 1f);
        animationX.setDuration(time);
        animationX.setInterpolator(new AccelerateInterpolator(2));
        animationY.setDuration(time);
        animationY.setInterpolator(new AccelerateInterpolator(2));
        animationX.start();
        animationY.start();
    }


    public static void translateObject(final View view, float x, float y) {
        int time = 200;
        float currX = view.getX();
        float currY = view.getY();
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "translationX", currX, x);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "translationY", currY, y);
        animationX.setDuration(time);
        animationX.setInterpolator(new AccelerateInterpolator(2));
        animationY.setDuration(time);
        animationY.setInterpolator(new AccelerateInterpolator(2));
        animationX.start();
        animationY.start();
    }
}
