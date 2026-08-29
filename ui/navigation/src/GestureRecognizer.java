package com.pegasus.ui.navigation;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;

/**
 * GestureRecognizer - Detects and processes touch gestures for navigation.
 * Recognizes swipes, taps, long presses, and other navigation gestures.
 */
public class GestureRecognizer extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = "GestureRecognizer";
    private static final float SWIPE_THRESHOLD = 100f; // pixels
    private static final float SWIPE_VELOCITY_THRESHOLD = 100f; // pixels/second
    private static final long LONG_PRESS_DURATION = 500L; // milliseconds

    private GestureDetector mGestureDetector;
    private OnGestureListener mGestureListener;

    private float mDownX, mDownY;
    private float mLastX, mLastY;
    private long mDownTime;

    /**
     * Interface for gesture detection callbacks
     */
    public interface OnGestureListener {
        void onGestureDetected(NavigationController.GestureType gestureType);
    }

    /**
     * Constructor
     */
    public GestureRecognizer(@NonNull Context context, @NonNull OnGestureListener listener) {
        this.mGestureListener = listener;
        this.mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(true);
    }

    /**
     * Process a touch event
     */
    public boolean processTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event);
                break;
        }

        return true;
    }

    /**
     * Handle touch down event
     */
    private void onTouchDown(MotionEvent event) {
        mDownX = event.getX();
        mDownY = event.getY();
        mLastX = mDownX;
        mLastY = mDownY;
        mDownTime = System.currentTimeMillis();
    }

    /**
     * Handle touch move event
     */
    private void onTouchMove(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
    }

    /**
     * Handle touch up event
     */
    private void onTouchUp(MotionEvent event) {
        float deltaX = event.getX() - mDownX;
        float deltaY = event.getY() - mDownY;
        long duration = System.currentTimeMillis() - mDownTime;

        // Check for swipes
        if (isSwipe(deltaX, deltaY, duration)) {
            if (isBackSwipe(deltaX, deltaY)) {
                mGestureListener.onGestureDetected(NavigationController.GestureType.BACK_SWIPE);
            } else if (isHomeSwipe(deltaX, deltaY)) {
                mGestureListener.onGestureDetected(NavigationController.GestureType.HOME_SWIPE);
            } else if (isRecentSwipe(deltaX, deltaY)) {
                mGestureListener.onGestureDetected(NavigationController.GestureType.RECENT_SWIPE);
            }
        }
    }

    /**
     * Handle touch cancel event
     */
    private void onTouchCancel(MotionEvent event) {
        // Reset state
        mDownX = 0;
        mDownY = 0;
        mLastX = 0;
        mLastY = 0;
        mDownTime = 0;
    }

    /**
     * Check if touch represents a swipe gesture
     */
    private boolean isSwipe(float deltaX, float deltaY, long duration) {
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float velocity = distance / (duration / 1000f);

        return distance > SWIPE_THRESHOLD && velocity > SWIPE_VELOCITY_THRESHOLD;
    }

    /**
     * Check if swipe is a back gesture (left swipe from left edge)
     */
    private boolean isBackSwipe(float deltaX, float deltaY) {
        return deltaX > 0 && Math.abs(deltaX) > Math.abs(deltaY) && mDownX < 50; // From left edge
    }

    /**
     * Check if swipe is a home gesture (up swipe from bottom)
     */
    private boolean isHomeSwipe(float deltaX, float deltaY) {
        return deltaY < 0 && Math.abs(deltaY) > Math.abs(deltaX); // Upward swipe
    }

    /**
     * Check if swipe is a recent apps gesture (right swipe from right edge)
     */
    private boolean isRecentSwipe(float deltaX, float deltaY) {
        return deltaX < 0 && Math.abs(deltaX) > Math.abs(deltaY); // Right-to-left swipe
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mGestureListener.onGestureDetected(NavigationController.GestureType.DOUBLE_TAP);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        mGestureListener.onGestureDetected(NavigationController.GestureType.LONG_PRESS);
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        // Cleanup if needed
    }
}
