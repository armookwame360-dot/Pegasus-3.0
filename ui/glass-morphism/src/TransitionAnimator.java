package com.pegasus.ui.glass;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import androidx.annotation.NonNull;

/**
 * TransitionAnimator - Orchestrates smooth transitions between UI states.
 * Provides choreographed animations for glass effects, fades, and slides.
 */
public class TransitionAnimator {
    private static final String TAG = "TransitionAnimator";
    private static final long DEFAULT_DURATION = 350L; // milliseconds

    private ValueAnimator mCurrentAnimator;
    private long mDuration = DEFAULT_DURATION;
    private TransitionListener mTransitionListener;

    /**
     * Interface for transition callbacks
     */
    public interface TransitionListener {
        void onTransitionStart();
        void onTransitionUpdate(float progress);
        void onTransitionEnd();
    }

    /**
     * Constructor
     */
    public TransitionAnimator() {
        this(DEFAULT_DURATION);
    }

    /**
     * Constructor with custom duration
     */
    public TransitionAnimator(long duration) {
        this.mDuration = duration;
    }

    /**
     * Start a transition animation
     */
    public void startTransition(@NonNull TransitionListener listener) {
        this.mTransitionListener = listener;
        cancelCurrentAnimation();

        if (listener != null) {
            listener.onTransitionStart();
        }

        mCurrentAnimator = ValueAnimator.ofFloat(0f, 1f);
        mCurrentAnimator.setDuration(mDuration);
        mCurrentAnimator.setInterpolator(new DecelerateInterpolator());

        mCurrentAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            if (listener != null) {
                listener.onTransitionUpdate(progress);
            }
        });

        mCurrentAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (listener != null) {
                    listener.onTransitionEnd();
                }
            }
        });

        mCurrentAnimator.start();
    }

    /**
     * Start a glass morphism appearance transition
     */
    public void glassAppear(@NonNull GlassEffectView view, @NonNull TransitionListener listener) {
        startTransition(new TransitionListener() {
            @Override
            public void onTransitionStart() {
                view.setAlpha(0f);
                view.setBlurRadius(0f);
                if (listener != null) listener.onTransitionStart();
            }

            @Override
            public void onTransitionUpdate(float progress) {
                // Animate blur radius and alpha together
                view.setBlurRadius(15f * progress);
                view.setAlpha(0.8f * progress);
                if (listener != null) listener.onTransitionUpdate(progress);
            }

            @Override
            public void onTransitionEnd() {
                view.setBlurRadius(15f);
                view.setAlpha(0.8f);
                if (listener != null) listener.onTransitionEnd();
            }
        });
    }

    /**
     * Start a glass morphism disappearance transition
     */
    public void glassDisappear(@NonNull GlassEffectView view, @NonNull TransitionListener listener) {
        startTransition(new TransitionListener() {
            @Override
            public void onTransitionStart() {
                if (listener != null) listener.onTransitionStart();
            }

            @Override
            public void onTransitionUpdate(float progress) {
                view.setBlurRadius(15f * (1 - progress));
                view.setAlpha(0.8f * (1 - progress));
                if (listener != null) listener.onTransitionUpdate(progress);
            }

            @Override
            public void onTransitionEnd() {
                view.setBlurRadius(0f);
                view.setAlpha(0f);
                if (listener != null) listener.onTransitionEnd();
            }
        });
    }

    /**
     * Set animation duration
     */
    public void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * Get animation duration
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * Cancel current animation
     */
    private void cancelCurrentAnimation() {
        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()) {
            mCurrentAnimator.cancel();
        }
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        cancelCurrentAnimation();
        mTransitionListener = null;
    }
}
