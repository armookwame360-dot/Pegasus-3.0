package com.pegasus.ui.icons;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * IconPack - Defines a themed icon pack with app and system icons.
 * Allows easy switching between different icon styles and themes.
 */
public class IconPack {
    public String name = "Default Icon Pack";
    public String version = "1.0";
    public String author = "Pegasus";
    public String description = "Default Pegasus icon pack";
    public String themeColor = "#1F88D8"; // Pegasus blue

    private Map<String, Integer> mAppIcons;
    private Map<String, Integer> mSystemIcons;
    private int mDefaultIconResource;

    /**
     * Constructor
     */
    public IconPack() {
        mAppIcons = new HashMap<>();
        mSystemIcons = new HashMap<>();
        initializeDefaultIcons();
    }

    /**
     * Initialize with default icon mappings
     */
    private void initializeDefaultIcons() {
        // Default app icon mappings
        mAppIcons.put("com.android.systemui", android.R.drawable.ic_launcher_home);
        mAppIcons.put("com.android.phone", android.R.drawable.ic_menu_call);
        mAppIcons.put("com.android.contacts", android.R.drawable.ic_menu_contact_list);
        mAppIcons.put("com.android.settings", android.R.drawable.ic_menu_manage);
        mAppIcons.put("com.android.chrome", android.R.drawable.ic_launcher_web_browser);
        mAppIcons.put("com.google.android.gms", android.R.drawable.ic_menu_myplaces);

        // System icons
        mSystemIcons.put("back", android.R.drawable.ic_media_previous);
        mSystemIcons.put("home", android.R.drawable.ic_launcher_home);
        mSystemIcons.put("recent", android.R.drawable.ic_menu_recent_history);
        mSystemIcons.put("close", android.R.drawable.ic_menu_close_clear_cancel);
        mSystemIcons.put("menu", android.R.drawable.ic_menu_more);

        mDefaultIconResource = android.R.drawable.ic_launcher_home;
    }

    /**
     * Get icon for app package
     */
    @Nullable
    public Drawable getIcon(@NonNull String packageName) {
        Integer resourceId = mAppIcons.get(packageName);
        if (resourceId != null) {
            try {
                return android.content.res.Resources.getSystem().getDrawable(resourceId);
            } catch (Exception e) {
                android.util.Log.e("IconPack", "Failed to load icon for " + packageName, e);
            }
        }
        return null;
    }

    /**
     * Get system icon
     */
    @Nullable
    public Drawable getSystemIcon(@NonNull String iconName) {
        Integer resourceId = mSystemIcons.get(iconName);
        if (resourceId != null) {
            try {
                return android.content.res.Resources.getSystem().getDrawable(resourceId);
            } catch (Exception e) {
                android.util.Log.e("IconPack", "Failed to load system icon " + iconName, e);
            }
        }
        return null;
    }

    /**
     * Get default/fallback icon
     */
    @Nullable
    public Drawable getDefaultIcon() {
        try {
            return android.content.res.Resources.getSystem().getDrawable(mDefaultIconResource);
        } catch (Exception e) {
            android.util.Log.e("IconPack", "Failed to load default icon", e);
            return null;
        }
    }

    /**
     * Register app icon
     */
    public void registerAppIcon(@NonNull String packageName, int resourceId) {
        mAppIcons.put(packageName, resourceId);
    }

    /**
     * Register system icon
     */
    public void registerSystemIcon(@NonNull String iconName, int resourceId) {
        mSystemIcons.put(iconName, resourceId);
    }

    /**
     * Get all app icon packages
     */
    public java.util.Set<String> getAppPackages() {
        return new java.util.HashSet<>(mAppIcons.keySet());
    }

    /**
     * Get metadata
     */
    public String getMetadata() {
        return String.format(
            "Name: %s\nVersion: %s\nAuthor: %s\nTheme: %s\nDescription: %s",
            name, version, author, themeColor, description
        );
    }
}
