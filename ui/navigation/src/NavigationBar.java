package com.pegasus.ui.navigation;

import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.annotation.NonNull;

/**
 * NavigationBar - Custom navigation bar view with back, home, and recent buttons.
 * Implements Pegasus design language with glass morphism effects.
 */
public class NavigationBar extends FrameLayout {
    private static final String TAG = "NavigationBar";

    private ImageButton mBackButton;
    private ImageButton mHomeButton;
    private ImageButton mRecentButton;
    private View mIndicatorView;
    private GlassEffectView mBackgroundGlass;

    private OnClickListener mBackClickListener;
    private OnClickListener mHomeClickListener;
    private OnClickListener mRecentClickListener;

    /**
     * Constructor
     */
    public NavigationBar(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    /**
     * Constructor with attributes
     */
    public NavigationBar(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    /**
     * Constructor with attributes and style
     */
    public NavigationBar(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Initialize navigation bar components
     */
    private void initialize(Context context) {
        // Inflate layout if using XML, or create programmatically
        // For now, creating programmatically
        setupBackgroundGlass();
        setupButtons();
        setupIndicator();
    }

    /**
     * Setup glass morphism background
     */
    private void setupBackgroundGlass() {
        mBackgroundGlass = new GlassEffectView(getContext());
        mBackgroundGlass.setBlurRadius(10f);
        mBackgroundGlass.setAlpha(0.9f);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        addView(mBackgroundGlass, params);
    }

    /**
     * Setup navigation buttons
     */
    private void setupButtons() {
        int buttonSize = dpToPx(48);
        int navBarHeight = dpToPx(56);

        // Back button
        mBackButton = createButton();
        LayoutParams backParams = new LayoutParams(buttonSize, buttonSize);
        backParams.gravity = android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.LEFT;
        backParams.leftMargin = dpToPx(16);
        addView(mBackButton, backParams);

        // Home button
        mHomeButton = createButton();
        LayoutParams homeParams = new LayoutParams(buttonSize, buttonSize);
        homeParams.gravity = android.view.Gravity.CENTER;
        addView(mHomeButton, homeParams);

        // Recent button
        mRecentButton = createButton();
        LayoutParams recentParams = new LayoutParams(buttonSize, buttonSize);
        recentParams.gravity = android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.RIGHT;
        recentParams.rightMargin = dpToPx(16);
        addView(mRecentButton, recentParams);

        // Set click listeners
        mBackButton.setOnClickListener(v -> {
            if (mBackClickListener != null) {
                mBackClickListener.onClick(v);
            }
        });

        mHomeButton.setOnClickListener(v -> {
            if (mHomeClickListener != null) {
                mHomeClickListener.onClick(v);
            }
        });

        mRecentButton.setOnClickListener(v -> {
            if (mRecentClickListener != null) {
                mRecentClickListener.onClick(v);
            }
        });
    }

    /**
     * Setup indicator view for navigation state
     */
    private void setupIndicator() {
        mIndicatorView = new View(getContext());
        mIndicatorView.setBackgroundColor(0xFF1F88D8); // Pegasus blue
        LayoutParams params = new LayoutParams(4, dpToPx(24));
        params.gravity = android.view.Gravity.CENTER | android.view.Gravity.BOTTOM;
        params.bottomMargin = dpToPx(8);
        addView(mIndicatorView, params);
    }

    /**
     * Create a styled button
     */
    private ImageButton createButton() {
        ImageButton button = new ImageButton(getContext());
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        button.setElevation(4f);
        return button;
    }

    /**
     * Set back button click listener
     */
    public void setOnBackClickListener(OnClickListener listener) {
        mBackClickListener = listener;
    }

    /**
     * Set home button click listener
     */
    public void setOnHomeClickListener(OnClickListener listener) {
        mHomeClickListener = listener;
    }

    /**
     * Set recent button click listener
     */
    public void setOnRecentClickListener(OnClickListener listener) {
        mRecentClickListener = listener;
    }

    /**
     * Update indicator position based on state
     */
    public void updateIndicatorPosition(NavigationController.NavigationState state) {
        float translationX = 0;
        switch (state) {
            case HOME:
                translationX = 0; // Center
                break;
            case APP:
                translationX = 0;
                break;
            case RECENT_APPS:
                translationX = dpToPx(50);
                break;
        }
        mIndicatorView.animate().translationX(translationX).setDuration(300).start();
    }

    /**
     * Convert DP to pixels
     */
    private int dpToPx(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }
}
