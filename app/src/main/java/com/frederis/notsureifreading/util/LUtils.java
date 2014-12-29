package com.frederis.notsureifreading.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.frederis.notsureifreading.R;

public class LUtils {

    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};
    private static final int[] STATE_UNCHECKED = new int[]{};

    private static boolean hasL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    protected Context mContext;

    private Handler mHandler = new Handler();

    public LUtils(Context context) {
        mContext = context;
    }


    public void setOrAnimateWordIdentifiedIcon(final ImageView imageView, boolean isCheck,
                                               boolean allowAnimate) {
        if (!hasL()) {
            compatSetOrAnimateWordIdentifiedIcon(imageView, isCheck, allowAnimate);
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof AnimatedStateListDrawable)) {
            drawable = mContext.getResources().getDrawable(R.drawable.add_schedule_fab_icon_anim);
            imageView.setImageDrawable(drawable);
        }
        imageView.setColorFilter(isCheck ?
                mContext.getResources().getColor(R.color.accent) : Color.WHITE);
        if (allowAnimate) {
            imageView.setImageState(isCheck ? STATE_UNCHECKED : STATE_CHECKED, false);
            drawable.jumpToCurrentState();
            imageView.setImageState(isCheck ? STATE_CHECKED : STATE_UNCHECKED, false);
        } else {
            imageView.setImageState(isCheck ? STATE_CHECKED : STATE_UNCHECKED, false);
            drawable.jumpToCurrentState();
        }
    }

    public void compatSetOrAnimateWordIdentifiedIcon(final ImageView imageView, boolean isCheck,
                                                     boolean allowAnimate) {

        final int imageResId = isCheck
                ? R.drawable.word_identified_fab_icon_checked
                : R.drawable.word_identified_fab_icon_unchecked;

        if (imageView.getTag() != null) {
            if (imageView.getTag() instanceof Animator) {
                Animator anim = (Animator) imageView.getTag();
                anim.end();
                imageView.setAlpha(1f);
            }
        }

        if (allowAnimate && isCheck) {
            int duration = mContext.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            Animator outAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f);
            outAnimator.setDuration(duration / 2);
            outAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imageView.setImageResource(imageResId);
                }
            });

            AnimatorSet inAnimator = new AnimatorSet();
            outAnimator.setDuration(duration);
            inAnimator.playTogether(
                    ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f),
                    ObjectAnimator.ofFloat(imageView, View.SCALE_X, 0f, 1f),
                    ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 0f, 1f)
            );

            AnimatorSet set = new AnimatorSet();
            set.playSequentially(outAnimator, inAnimator);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imageView.setTag(null);
                }
            });
            imageView.setTag(set);
            set.start();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageResource(imageResId);
                }
            });
        }
    }


}
