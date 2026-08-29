package com.pegasus.ui.glass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.annotation.NonNull;

/**
 * BlurRenderer - High-performance blur rendering using RenderScript.
 * Provides various blur algorithms with GPU acceleration when available.
 */
public class BlurRenderer {
    private static final String TAG = "BlurRenderer";
    private static final int MAX_BLUR_RADIUS = 25;

    private Context mContext;
    private RenderScript mRenderScript;
    private Bitmap mBlurredBitmap;
    private boolean mUseRenderScript = true;

    /**
     * Constructor
     */
    public BlurRenderer(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        try {
            mRenderScript = RenderScript.create(mContext);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to initialize RenderScript", e);
            mUseRenderScript = false;
        }
    }

    /**
     * Render blur effect on canvas
     */
    public void renderBlur(@NonNull Canvas canvas, int width, int height, float blurRadius) {
        if (mUseRenderScript && mRenderScript != null) {
            renderBlurWithRenderScript(canvas, width, height, blurRadius);
        } else {
            renderBlurSoftware(canvas, width, height, blurRadius);
        }
    }

    /**
     * Render blur using RenderScript (GPU-accelerated)
     */
    private void renderBlurWithRenderScript(Canvas canvas, int width, int height, float blurRadius) {
        try {
            // Clamp blur radius to max
            float radius = Math.min(blurRadius, MAX_BLUR_RADIUS);

            // Create input bitmap from current canvas
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(bitmap);
            super.draw(tempCanvas);

            // Create RenderScript allocation
            Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap);
            Allocation output = Allocation.createTyped(mRenderScript, input.getType());

            // Apply blur
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            blurScript.setRadius(radius);
            blurScript.setInput(input);
            blurScript.forEach(output);

            // Copy result back
            mBlurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            output.copyTo(mBlurredBitmap);

            // Draw blurred bitmap
            canvas.drawBitmap(mBlurredBitmap, 0, 0, null);

            // Cleanup
            input.destroy();
            output.destroy();
            blurScript.destroy();
            bitmap.recycle();
        } catch (Exception e) {
            android.util.Log.e(TAG, "RenderScript blur failed, falling back to software", e);
            renderBlurSoftware(canvas, width, height, blurRadius);
        }
    }

    /**
     * Render blur using software algorithm (fallback)
     */
    private void renderBlurSoftware(Canvas canvas, int width, int height, float blurRadius) {
        // Stack blur algorithm - fast software implementation
        try {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Bitmap blurred = fastBlur(bitmap, (int) blurRadius);
            canvas.drawBitmap(blurred, 0, 0, null);
            bitmap.recycle();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Software blur failed", e);
        }
    }

    /**
     * Fast box blur implementation
     */
    private Bitmap fastBlur(Bitmap bitmap, int radius) {
        if (radius <= 0) {
            return bitmap;
        }

        // Simplified box blur for performance
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Apply horizontal blur
        int[] temp = new int[pixels.length];
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int sum = 0;
                int count = 0;
                for (int dx = -radius; dx <= radius; dx++) {
                    int nx = Math.max(0, Math.min(bitmap.getWidth() - 1, x + dx));
                    sum += pixels[y * bitmap.getWidth() + nx];
                    count++;
                }
                temp[y * bitmap.getWidth() + x] = sum / count;
            }
        }

        // Apply vertical blur
        int[] result = new int[pixels.length];
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int sum = 0;
                int count = 0;
                for (int dy = -radius; dy <= radius; dy++) {
                    int ny = Math.max(0, Math.min(bitmap.getHeight() - 1, y + dy));
                    sum += temp[ny * bitmap.getWidth() + x];
                    count++;
                }
                result[y * bitmap.getWidth() + x] = sum / count;
            }
        }

        output.setPixels(result, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return output;
    }

    /**
     * Set whether to use RenderScript
     */
    public void setUseRenderScript(boolean use) {
        mUseRenderScript = use;
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        if (mBlurredBitmap != null) {
            mBlurredBitmap.recycle();
            mBlurredBitmap = null;
        }
        if (mRenderScript != null) {
            mRenderScript.destroy();
            mRenderScript = null;
        }
    }
}
