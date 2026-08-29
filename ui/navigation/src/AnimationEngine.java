package com.pegasus.ui.navigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.NonNull;

/**
 * AnimationEngine - Handles smooth animations for navigation transitions.
 * Provides various animation effects including fades, slides, and scales.
 */
public class AnimationEngine {
    private static final String TAG = "AnimationEngine";

    private long mAnimationDuration;
    private Animator mCurrentAnimator;

    /**
     * Animation callback interface
     */
    public interface AnimationCallback {
        void onAnimationComplete();
    }

    /**
     * Constructor
     */
    public AnimationEngine(long animationDuration) {
        this.mAnimationDuration = animationDuration;
    }

    /**
     * Play a transition animation
     */
    public void playTransition(@NonNull AnimationCallback callback) {
        cancelCurrentAnimation();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(mAnimationDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                // Update UI based on progress
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onAnimationComplete();
            }
        });

        mCurrentAnimator = animator;
        animator.start();
    }

    /**
     * Play fade in animation
     */
    public void playFadeIn(android.view.View view, @NonNull AnimationCallback callback) {
        cancelCurrentAnimation();

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(mAnimationDuration);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onAnimationComplete();
            }
        });

        mCurrentAnimator = fadeIn;
        fadeIn.start();
    }

    /**
     * Play fade out animation
     */
    public void playFadeOut(android.view.View view, @NonNull AnimationCallback callback) {
        cancelCurrentAnimation();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(mAnimationDuration);
        fadeOut.setInterpolator(new DecelerateInterpolator());

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onAnimationComplete();
            }
        });

        mCurrentAnimator = fadeOut;
        fadeOut.start();
    }

    /**
     * Play slide animation
     */
    public void playSlide(android.view.View view, float fromX, float toX,
                         @NonNull AnimationCallback callback) {
        cancelCurrentAnimation();

        ObjectAnimator slide = ObjectAnimator.ofFloat(view, "translationX", fromX, toX);
        slide.setDuration(mAnimationDuration);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());

        slide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onAnimationComplete();
            }
        });

        mCurrentAnimator = slide;
        slide.start();
    }

    /**
     * Play scale animation
     */
    public void playScale(android.view.View view, float fromScale, float toScale,
                         @NonNull AnimationCallback callback) {
        cancelCurrentAnimation();

        android.animation.AnimatorSet animatorSet = new android.animation.AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", fromScale, toScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", fromScale, toScale);

        scaleX.setDuration(mAnimationDuration);
        scaleY.setDuration(mAnimationDuration);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());

        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onAnimationComplete();
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }

    /**
     * Cancel current running animation
     */
    private void cancelCurrentAnimation() {
        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()) {
            mCurrentAnimator.cancel();
        }
    }

    /**
     * Set animation duration
     */
    public void setAnimationDuration(long duration) {
        mAnimationDuration = duration;
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        cancelCurrentAnimation();
    }
}
