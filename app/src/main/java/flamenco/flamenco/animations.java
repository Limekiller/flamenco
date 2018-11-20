package flamenco.flamenco;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
}
