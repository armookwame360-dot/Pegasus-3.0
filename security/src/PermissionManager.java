package com.pegasus.security;

import android.content.Context;
import android.content.pm.PermissionInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PermissionManager - Manages app permissions with enhanced auditing and control.
 * Tracks permission requests, usage, and provides granular permission control.
 */
public class PermissionManager {
    private static final String TAG = "PegasusPermission";

    private Context mContext;
    private PermissionAuditor mAuditor;
    private PermissionValidator mValidator;
    private Map<String, PermissionPolicy> mPolicies;

    /**
     * Permission policy levels
     */
    public enum PermissionPolicy {
        ALLOW,           // Always allow
        DENY,            // Always deny
        ASK_EVERY_TIME,  // Ask user each time
        ALLOW_ONLY_WHILE_USING,  // Allow only when app is in foreground
        MONITOR          // Allow but monitor usage
    }

    /**
     * Permission request callback
     */
    public interface PermissionCallback {
        void onPermissionResult(String permission, boolean granted);
    }

    /**
     * Constructor
     */
    public PermissionManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mPolicies = new HashMap<>();
        this.mAuditor = new PermissionAuditor(context);
        this.mValidator = new PermissionValidator(context);
        initializeDefaultPolicies();
    }

    /**
     * Initialize default permission policies
     */
    private void initializeDefaultPolicies() {
        // Sensitive permissions default to ASK_EVERY_TIME
        String[] sensitivePermissions = {
                "android.permission.CAMERA",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.RECORD_AUDIO",
                "android.permission.ACCESS_CONTACTS",
                "android.permission.READ_CALL_LOG",
                "android.permission.READ_SMS"
        };

        for (String permission : sensitivePermissions) {
            mPolicies.put(permission, PermissionPolicy.MONITOR);
        }
    }

    /**
     * Request a permission
     */
    public void requestPermission(@NonNull String permission,
                                 @NonNull String packageName,
                                 @NonNull PermissionCallback callback) {
        // Log the request
        mAuditor.logPermissionRequest(packageName, permission);

        // Check policy for this permission
        PermissionPolicy policy = mPolicies.getOrDefault(permission, PermissionPolicy.ASK_EVERY_TIME);

        switch (policy) {
            case ALLOW:
                handlePermissionGranted(packageName, permission, callback);
                break;
            case DENY:
                handlePermissionDenied(packageName, permission, callback);
                break;
            case ASK_EVERY_TIME:
                promptUser(packageName, permission, callback);
                break;
            case ALLOW_ONLY_WHILE_USING:
                if (isAppInForeground(packageName)) {
                    handlePermissionGranted(packageName, permission, callback);
                } else {
                    handlePermissionDenied(packageName, permission, callback);
                }
                break;
            case MONITOR:
                handlePermissionGranted(packageName, permission, callback);
                mAuditor.startMonitoring(packageName, permission);
                break;
        }
    }

    /**
     * Set permission policy for an app
     */
    public void setPermissionPolicy(@NonNull String permission,
                                    @NonNull PermissionPolicy policy) {
        mPolicies.put(permission, policy);
        mAuditor.logPolicyChange(permission, policy);
    }

    /**
     * Get permission policy
     */
    public PermissionPolicy getPermissionPolicy(@NonNull String permission) {
        return mPolicies.getOrDefault(permission, PermissionPolicy.ASK_EVERY_TIME);
    }

    /**
     * Handle permission granted
     */
    private void handlePermissionGranted(@NonNull String packageName,
                                        @NonNull String permission,
                                        @NonNull PermissionCallback callback) {
        mAuditor.logPermissionGrant(packageName, permission);
        callback.onPermissionResult(permission, true);
    }

    /**
     * Handle permission denied
     */
    private void handlePermissionDenied(@NonNull String packageName,
                                       @NonNull String permission,
                                       @NonNull PermissionCallback callback) {
        mAuditor.logPermissionDenial(packageName, permission);
        callback.onPermissionResult(permission, false);
    }

    /**
     * Prompt user for permission
     */
    private void promptUser(@NonNull String packageName,
                           @NonNull String permission,
                           @NonNull PermissionCallback callback) {
        // This would integrate with system permission dialog
        android.util.Log.d(TAG, "Prompting user for permission: " + permission);
        // In real implementation, show system permission dialog
    }

    /**
     * Check if app is in foreground
     */
    private boolean isAppInForeground(@NonNull String packageName) {
        android.app.ActivityManager activityManager =
                (android.app.ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            java.util.List<android.app.ActivityManager.RunningTaskInfo> tasks =
                    activityManager.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                return tasks.get(0).topActivity.getPackageName().equals(packageName);
            }
        }
        return false;
    }

    /**
     * Get audit logs for a package
     */
    public java.util.List<String> getAuditLogs(@NonNull String packageName) {
        return mAuditor.getLogsForPackage(packageName);
    }

    /**
     * Clear audit logs
     */
    public void clearAuditLogs() {
        mAuditor.clearLogs();
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        if (mAuditor != null) {
            mAuditor.destroy();
        }
        mPolicies.clear();
    }
}
