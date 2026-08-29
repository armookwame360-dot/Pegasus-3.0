package com.pegasus.security;

import android.content.Context;
import android.content.pm.PermissionInfo;
import androidx.annotation.NonNull;

/**
 * PermissionValidator - Validates permission requests against system policies.
 * Ensures permission consistency and blocks dangerous permission combinations.
 */
public class PermissionValidator {
    private static final String TAG = "PermissionValidator";

    private Context mContext;

    // Dangerous permission combinations that should be blocked together
    private static final String[][] DANGEROUS_COMBINATIONS = {
        {"android.permission.CAMERA", "android.permission.RECORD_AUDIO"},
        {"android.permission.ACCESS_FINE_LOCATION", "android.permission.RECORD_AUDIO"},
        {"android.permission.READ_CONTACTS", "android.permission.READ_CALL_LOG"}
    };

    /**
     * Constructor
     */
    public PermissionValidator(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    /**
     * Validate a permission request
     */
    public boolean isPermissionValid(@NonNull String permission) {
        // Check if permission exists in system
        try {
            PermissionInfo info = mContext.getPackageManager()
                    .getPermissionInfo(permission, 0);
            return info != null;
        } catch (Exception e) {
            android.util.Log.e(TAG, "Invalid permission: " + permission, e);
            return false;
        }
    }

    /**
     * Check for dangerous permission combinations
     */
    public boolean isDangerousCombination(@NonNull String permission1, @NonNull String permission2) {
        for (String[] combination : DANGEROUS_COMBINATIONS) {
            if ((combination[0].equals(permission1) && combination[1].equals(permission2)) ||
                (combination[0].equals(permission2) && combination[1].equals(permission1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if permission is dangerous
     */
    public boolean isDangerousPermission(@NonNull String permission) {
        String[] dangerousPermissions = {
            "android.permission.CAMERA",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_CONTACTS",
            "android.permission.READ_CALL_LOG",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.READ_CALENDAR"
        };

        for (String dangerous : dangerousPermissions) {
            if (dangerous.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        // Cleanup if needed
    }
}
