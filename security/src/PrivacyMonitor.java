package com.pegasus.security;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PrivacyMonitor - Real-time monitoring of sensitive resource access.
 * Tracks camera, microphone, location, and network access.
 */
public class PrivacyMonitor {
    private static final String TAG = "PrivacyMonitor";

    private Context mContext;
    private Map<String, ResourceMonitor> mActiveMonitors;
    private List<PrivacyEvent> mEventLog;
    private PrivacyListener mListener;

    /**
     * Privacy event types
     */
    public enum PrivacyEventType {
        CAMERA_ACCESS,
        MICROPHONE_ACCESS,
        LOCATION_ACCESS,
        CONTACT_ACCESS,
        STORAGE_ACCESS,
        NETWORK_ACCESS
    }

    /**
     * Privacy event
     */
    public static class PrivacyEvent {
        public String packageName;
        public PrivacyEventType eventType;
        public long timestamp;
        public long duration;
        public boolean allowed;

        public PrivacyEvent(String packageName, PrivacyEventType eventType) {
            this.packageName = packageName;
            this.eventType = eventType;
            this.timestamp = System.currentTimeMillis();
            this.duration = 0;
            this.allowed = false;
        }
    }

    /**
     * Interface for privacy event listening
     */
    public interface PrivacyListener {
        void onPrivacyEvent(PrivacyEvent event);
        void onSuspiciousActivity(String packageName, PrivacyEventType type);
    }

    /**
     * Internal resource monitor
     */
    private class ResourceMonitor {
        String packageName;
        PrivacyEventType type;
        long startTime;
        boolean active;

        ResourceMonitor(String packageName, PrivacyEventType type) {
            this.packageName = packageName;
            this.type = type;
            this.startTime = System.currentTimeMillis();
            this.active = true;
        }
    }

    /**
     * Constructor
     */
    public PrivacyMonitor(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mActiveMonitors = new HashMap<>();
        this.mEventLog = new ArrayList<>();
    }

    /**
     * Start monitoring a resource access
     */
    public void startMonitoring(@NonNull String packageName, @NonNull PrivacyEventType type) {
        String monitorKey = packageName + ":" + type.name();
        ResourceMonitor monitor = new ResourceMonitor(packageName, type);
        mActiveMonitors.put(monitorKey, monitor);

        android.util.Log.d(TAG, "Started monitoring " + type + " for " + packageName);
    }

    /**
     * Stop monitoring a resource access
     */
    public void stopMonitoring(@NonNull String packageName, @NonNull PrivacyEventType type) {
        String monitorKey = packageName + ":" + type.name();
        ResourceMonitor monitor = mActiveMonitors.remove(monitorKey);

        if (monitor != null) {
            PrivacyEvent event = new PrivacyEvent(packageName, type);
            event.duration = System.currentTimeMillis() - monitor.startTime;
            event.allowed = true;
            mEventLog.add(event);

            if (mListener != null) {
                mListener.onPrivacyEvent(event);
            }

            // Check for suspicious activity
            checkSuspiciousActivity(event);
        }
    }

    /**
     * Check for suspicious activity patterns
     */
    private void checkSuspiciousActivity(PrivacyEvent event) {
        // Check if app is using camera/mic in background
        if ((event.eventType == PrivacyEventType.CAMERA_ACCESS ||
             event.eventType == PrivacyEventType.MICROPHONE_ACCESS) &&
            !isAppInForeground(event.packageName)) {
            
            if (mListener != null) {
                mListener.onSuspiciousActivity(event.packageName, event.eventType);
            }
        }

        // Check for excessive location access
        if (event.eventType == PrivacyEventType.LOCATION_ACCESS &&
            event.duration > 60000) { // More than 1 minute
            
            if (mListener != null) {
                mListener.onSuspiciousActivity(event.packageName, event.eventType);
            }
        }
    }

    /**
     * Check if app is in foreground
     */
    private boolean isAppInForeground(@NonNull String packageName) {
        android.app.ActivityManager activityManager =
                (android.app.ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<android.app.ActivityManager.RunningTaskInfo> tasks =
                    activityManager.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                return tasks.get(0).topActivity.getPackageName().equals(packageName);
            }
        }
        return false;
    }

    /**
     * Get all monitored events
     */
    public List<PrivacyEvent> getEventLog() {
        return new ArrayList<>(mEventLog);
    }

    /**
     * Get events for specific package
     */
    public List<PrivacyEvent> getEventsForPackage(@NonNull String packageName) {
        List<PrivacyEvent> events = new ArrayList<>();
        for (PrivacyEvent event : mEventLog) {
            if (event.packageName.equals(packageName)) {
                events.add(event);
            }
        }
        return events;
    }

    /**
     * Set privacy event listener
     */
    public void setListener(@NonNull PrivacyListener listener) {
        mListener = listener;
    }

    /**
     * Clear event log
     */
    public void clearEventLog() {
        mEventLog.clear();
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        mActiveMonitors.clear();
        mEventLog.clear();
        mListener = null;
    }
}
