package com.pegasus.ui.icons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * IconProvider - Delivers styled app icons from custom icon pack.
 * Supports multiple icon themes and dynamic icon customization.
 */
public class IconProvider {
    private static final String TAG = "IconProvider";

    private Context mContext;
    private IconPack mCurrentIconPack;
    private Map<String, IconEntry> mIconCache;
    private IconCache mDiskCache;

    /**
     * Icon pack entry
     */
    private static class IconEntry {
        String packageName;
        String iconName;
        int resourceId;
        Drawable drawable;

        IconEntry(String packageName, String iconName, int resourceId) {
            this.packageName = packageName;
            this.iconName = iconName;
            this.resourceId = resourceId;
        }
    }

    /**
     * Constructor
     */
    public IconProvider(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mIconCache = new HashMap<>();
        this.mDiskCache = new IconCache(context);
        loadDefaultIconPack();
    }

    /**
     * Load default icon pack
     */
    private void loadDefaultIconPack() {
        mCurrentIconPack = new IconPack();
        mCurrentIconPack.name = "Pegasus Default";
        mCurrentIconPack.version = "1.0";
        mCurrentIconPack.author = "Pegasus Team";
    }

    /**
     * Get icon for app package
     */
    @Nullable
    public Drawable getAppIcon(@NonNull String packageName) {
        // Check memory cache first
        IconEntry cached = mIconCache.get(packageName);
        if (cached != null && cached.drawable != null) {
            return cached.drawable;
        }

        // Check disk cache
        Drawable cached_disk = mDiskCache.getIcon(packageName);
        if (cached_disk != null) {
            IconEntry entry = new IconEntry(packageName, packageName, 0);
            entry.drawable = cached_disk;
            mIconCache.put(packageName, entry);
            return cached_disk;
        }

        // Load from icon pack
        Drawable drawable = mCurrentIconPack.getIcon(packageName);
        if (drawable != null) {
            // Cache it
            IconEntry entry = new IconEntry(packageName, packageName, 0);
            entry.drawable = drawable;
            mIconCache.put(packageName, entry);
            mDiskCache.saveIcon(packageName, drawable);
            return drawable;
        }

        // Return default/fallback icon
        return getDefaultIcon();
    }

    /**
     * Get system icon
     */
    @Nullable
    public Drawable getSystemIcon(@NonNull String iconName) {
        return mCurrentIconPack.getSystemIcon(iconName);
    }

    /**
     * Get default fallback icon
     */
    private Drawable getDefaultIcon() {
        return mCurrentIconPack.getDefaultIcon();
    }

    /**
     * Set current icon pack
     */
    public void setIconPack(@NonNull IconPack iconPack) {
        mCurrentIconPack = iconPack;
        mIconCache.clear(); // Clear cache when switching packs
    }

    /**
     * Get current icon pack
     */
    public IconPack getCurrentIconPack() {
        return mCurrentIconPack;
    }

    /**
     * Clear icon cache
     */
    public void clearCache() {
        mIconCache.clear();
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        mIconCache.clear();
        if (mDiskCache != null) {
            mDiskCache.destroy();
        }
    }
}
