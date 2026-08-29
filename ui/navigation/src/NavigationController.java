package com.pegasus.ui.navigation;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.animation.Animator;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * NavigationController - Core orchestrator for Pegasus gesture-based navigation system.
 * Manages navigation state, gestures, and transitions between applications.
 */
public class NavigationController {
    private static final String TAG = "PegasusNavigation";
    private static final int GESTURE_THRESHOLD = 50; // pixels
    private static final int ANIMATION_DURATION = 300; // ms

    private Context mContext;
    private NavigationBar mNavigationBar;
    private GestureRecognizer mGestureRecognizer;
    private BackgroundNavigationManager mBackgroundNavManager;
    private AnimationEngine mAnimationEngine;
    private List<NavigationStateListener> mListeners;

    private NavigationState mCurrentState = NavigationState.HOME;
    private boolean mBackgroundNavigationEnabled = true;

    /**
     * Enumeration of navigation states
     */
    public enum NavigationState {
        HOME,           // Home screen
        APP,            // Application
        RECENT_APPS,    // Recent apps view
        TRANSITIONING   // In transition between states
    }

    /**
     * Interface for listening to navigation state changes
     */
    public interface NavigationStateListener {
        void onNavigationStateChanged(NavigationState newState, NavigationState oldState);
        void onGestureDetected(GestureType gestureType);
    }

    /**
     * Types of gestures supported
     */
    public enum GestureType {
        BACK_SWIPE,
        HOME_SWIPE,
        RECENT_SWIPE,
        DOUBLE_TAP,
        LONG_PRESS
    }

    /**
     * Constructor
     */
    public NavigationController(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mListeners = new ArrayList<>();
        initialize();
    }

    /**
     * Initialize navigation components
     */
    private void initialize() {
        mGestureRecognizer = new GestureRecognizer(mContext, this::onGestureDetected);
        mBackgroundNavManager = new BackgroundNavigationManager(mContext);
        mAnimationEngine = new AnimationEngine(ANIMATION_DURATION);
        mNavigationBar = new NavigationBar(mContext);

        setupListeners();
    }

    /**
     * Setup internal listeners
     */
    private void setupListeners() {
        mNavigationBar.setOnBackClickListener(() -> handleBackNavigation());
        mNavigationBar.setOnHomeClickListener(() -> handleHomeNavigation());
        mNavigationBar.setOnRecentClickListener(() -> handleRecentAppsNavigation());
    }

    /**
     * Handle touch events from the system
     */
    public boolean handleTouchEvent(MotionEvent event) {
        return mGestureRecognizer.processTouchEvent(event);
    }

    /**
     * Called when a gesture is detected
     */
    private void onGestureDetected(GestureType gestureType) {
        notifyGestureDetected(gestureType);

        switch (gestureType) {
            case BACK_SWIPE:
                handleBackNavigation();
                break;
            case HOME_SWIPE:
                handleHomeNavigation();
                break;
            case RECENT_SWIPE:
                handleRecentAppsNavigation();
                break;
            case DOUBLE_TAP:
                handleDoubleTap();
                break;
            case LONG_PRESS:
                handleLongPress();
                break;
        }
    }

    /**
     * Handle back navigation
     */
    private void handleBackNavigation() {
        if (mCurrentState == NavigationState.TRANSITIONING) {
            return; // Prevent navigation during transition
        }

        NavigationState oldState = mCurrentState;
        setNavigationState(NavigationState.TRANSITIONING);

        if (mBackgroundNavigationEnabled) {
            // Let background navigation manager handle it
            mBackgroundNavManager.navigateBack(() -> {
                // Transition complete
                NavigationState newState = mBackgroundNavManager.getCurrentAppState();
                setNavigationState(newState);
                playTransitionAnimation();
            });
        } else {
            // Standard back navigation
            setNavigationState(oldState);
        }
    }

    /**
     * Handle home navigation
     */
    private void handleHomeNavigation() {
        NavigationState oldState = mCurrentState;
        setNavigationState(NavigationState.TRANSITIONING);

        // Save current app state for background navigation
        if (mBackgroundNavigationEnabled) {
            mBackgroundNavManager.saveAppState();
        }

        setNavigationState(NavigationState.HOME);
        playTransitionAnimation();
    }

    /**
     * Handle recent apps navigation
     */
    private void handleRecentAppsNavigation() {
        NavigationState oldState = mCurrentState;
        setNavigationState(NavigationState.TRANSITIONING);
        setNavigationState(NavigationState.RECENT_APPS);
        playTransitionAnimation();
    }

    /**
     * Handle double tap gesture
     */
    private void handleDoubleTap() {
        // Could trigger app switcher or split screen
        android.util.Log.d(TAG, "Double tap detected");
    }

    /**
     * Handle long press gesture
     */
    private void handleLongPress() {
        // Could trigger app drawer or shortcuts
        android.util.Log.d(TAG, "Long press detected");
    }

    /**
     * Play transition animation
     */
    private void playTransitionAnimation() {
        mAnimationEngine.playTransition(() -> {
            // Animation complete
            if (mCurrentState == NavigationState.TRANSITIONING) {
                setNavigationState(NavigationState.APP);
            }
        });
    }

    /**
     * Set navigation state and notify listeners
     */
    private void setNavigationState(NavigationState newState) {
        if (mCurrentState != newState) {
            NavigationState oldState = mCurrentState;
            mCurrentState = newState;
            notifyStateChanged(newState, oldState);
        }
    }

    /**
     * Get current navigation state
     */
    public NavigationState getCurrentState() {
        return mCurrentState;
    }

    /**
     * Set whether background navigation is enabled
     */
    public void setBackgroundNavigationEnabled(boolean enabled) {
        mBackgroundNavigationEnabled = enabled;
    }

    /**
     * Get navigation bar view
     */
    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    /**
     * Add state change listener
     */
    public void addStateListener(NavigationStateListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Remove state change listener
     */
    public void removeStateListener(NavigationStateListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Notify all listeners of state change
     */
    private void notifyStateChanged(NavigationState newState, NavigationState oldState) {
        for (NavigationStateListener listener : mListeners) {
            listener.onNavigationStateChanged(newState, oldState);
        }
    }

    /**
     * Notify all listeners of gesture detection
     */
    private void notifyGestureDetected(GestureType gestureType) {
        for (NavigationStateListener listener : mListeners) {
            listener.onGestureDetected(gestureType);
        }
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        mListeners.clear();
        if (mGestureRecognizer != null) {
            mGestureRecognizer.destroy();
        }
        if (mBackgroundNavManager != null) {
            mBackgroundNavManager.destroy();
        }
        if (mAnimationEngine != null) {
            mAnimationEngine.destroy();
        }
    }
}
