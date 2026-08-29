package com.pegasus.ui.glass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;

/**
 * GlassEffectView - Renders liquid glass morphism effects with blur and transparency.
 * Creates smooth, frosted glass appearance for UI elements.
 */
public class GlassEffectView extends View {
    private static final String TAG = "GlassEffectView";

    private Paint mGlassPaint;
    private BlurRenderer mBlurRenderer;
    private float mBlurRadius = 15f;
    private float mAlpha = 0.8f;
    private int mTintColor = 0x20FFFFFF; // Semi-transparent white tint

    /**
     * Constructor
     */
    public GlassEffectView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    /**
     * Constructor with attributes
     */
    public GlassEffectView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    /**
     * Constructor with attributes and style
     */
    public GlassEffectView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Initialize glass effect rendering
     */
    private void initialize(Context context) {
        setWillNotDraw(false);
        mBlurRenderer = new BlurRenderer(context);
        setupPaint();
    }

    /**
     * Setup paint for glass rendering
     */
    private void setupPaint() {
        mGlassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGlassPaint.setStyle(Paint.Style.FILL);
        mGlassPaint.setColor(mTintColor);
        mGlassPaint.setAlpha((int) (255 * mAlpha));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw blurred background
        if (mBlurRenderer != null) {
            mBlurRenderer.renderBlur(canvas, getWidth(), getHeight(), mBlurRadius);
        }

        // Draw tinted glass overlay
        canvas.drawRect(0, 0, getWidth(), getHeight(), mGlassPaint);

        // Draw subtle edge gradient for depth
        drawEdgeGradient(canvas);
    }

    /**
     * Draw subtle edge gradient for depth
     */
    private void drawEdgeGradient(Canvas canvas) {
        Paint edgePaint = new Paint();
        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(2f);
        edgePaint.setColor(0x30FFFFFF); // Bright edge highlight
        canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, edgePaint);

        edgePaint.setColor(0x1A000000); // Dark bottom edge shadow
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, edgePaint);
        canvas.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight(), edgePaint);
    }

    /**
     * Set blur radius for the glass effect
     */
    public void setBlurRadius(float radius) {
        mBlurRadius = radius;
        invalidate();
    }

    /**
     * Get current blur radius
     */
    public float getBlurRadius() {
        return mBlurRadius;
    }

    /**
     * Set alpha transparency
     */
    @Override
    public void setAlpha(float alpha) {
        mAlpha = alpha;
        if (mGlassPaint != null) {
            mGlassPaint.setAlpha((int) (255 * mAlpha));
        }
        super.setAlpha(alpha);
        invalidate();
    }

    /**
     * Set tint color
     */
    public void setTintColor(int color) {
        mTintColor = color;
        if (mGlassPaint != null) {
            mGlassPaint.setColor(mTintColor);
        }
        invalidate();
    }

    /**
     * Get tint color
     */
    public int getTintColor() {
        return mTintColor;
    }

    /**
     * Enable/disable glass effect
     */
    public void setGlassEffectEnabled(boolean enabled) {
        if (enabled) {
            setAlpha(mAlpha);
        } else {
            setAlpha(0f);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBlurRenderer != null) {
            mBlurRenderer.destroy();
        }
    }
}
