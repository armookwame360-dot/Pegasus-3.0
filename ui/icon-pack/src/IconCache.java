package com.pegasus.ui.icons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * IconCache - Manages caching of icons to disk for performance.
 * Reduces memory usage and improves app startup time.
 */
public class IconCache {
    private static final String TAG = "IconCache";
    private static final String ICON_CACHE_DIR = "icon_cache";

    private Context mContext;
    private File mCacheDir;

    /**
     * Constructor
     */
    public IconCache(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mCacheDir = new File(context.getCacheDir(), ICON_CACHE_DIR);
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    /**
     * Get cached icon
     */
    @Nullable
    public Drawable getIcon(@NonNull String packageName) {
        File iconFile = new File(mCacheDir, sanitizeFileName(packageName) + ".png");
        if (iconFile.exists()) {
            try {
                Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(iconFile.getAbsolutePath());
                if (bitmap != null) {
                    return new BitmapDrawable(mContext.getResources(), bitmap);
                }
            } catch (Exception e) {
                android.util.Log.e(TAG, "Failed to load cached icon", e);
            }
        }
        return null;
    }

    /**
     * Save icon to cache
     */
    public void saveIcon(@NonNull String packageName, @NonNull Drawable drawable) {
        try {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                // Convert drawable to bitmap
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                if (width <= 0) width = 128;
                if (height <= 0) height = 128;

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
            }

            if (bitmap != null) {
                File iconFile = new File(mCacheDir, sanitizeFileName(packageName) + ".png");
                try (FileOutputStream fos = new FileOutputStream(iconFile)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    android.util.Log.d(TAG, "Cached icon for " + packageName);
                }
            }
        } catch (IOException e) {
            android.util.Log.e(TAG, "Failed to save icon cache", e);
        }
    }

    /**
     * Remove icon from cache
     */
    public void removeIcon(@NonNull String packageName) {
        File iconFile = new File(mCacheDir, sanitizeFileName(packageName) + ".png");
        if (iconFile.exists()) {
            iconFile.delete();
        }
    }

    /**
     * Clear entire cache
     */
    public void clearCache() {
        File[] files = mCacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * Get cache size
     */
    public long getCacheSize() {
        long size = 0;
        File[] files = mCacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * Sanitize filename from package name
     */
    private String sanitizeFileName(@NonNull String packageName) {
        return packageName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        clearCache();
    }
}
