package com.pegasus.ui.navigation;

import android.app.ActivityManager;
import android.content.Context;
import androidx.annotation.NonNull;
import java.util.LinkedList;
import java.util.List;

/**
 * BackgroundNavigationManager - Manages application stack and background navigation.
 * Allows seamless switching between apps while maintaining their state.
 */
public class BackgroundNavigationManager {
    private static final String TAG = "BackgroundNavManager";
    private static final int MAX_STACK_SIZE = 10;

    private Context mContext;
    private LinkedList<AppStackEntry> mAppStack;
    private ActivityManager mActivityManager;

    /**
     * Represents an entry in the app stack
     */
    private static class AppStackEntry {
        String packageName;
        String activityName;
        long timestamp;
        NavigationController.NavigationState state;

        AppStackEntry(String packageName, String activityName, NavigationController.NavigationState state) {
            this.packageName = packageName;
            this.activityName = activityName;
            this.state = state;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Interface for navigation callbacks
     */
    public interface NavigationCallback {
        void onNavigationComplete();
    }

    /**
     * Constructor
     */
    public BackgroundNavigationManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mAppStack = new LinkedList<>();
        this.mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * Save current app state
     */
    public void saveAppState() {
        ActivityManager.RunningTaskInfo currentTask = getCurrentTask();
        if (currentTask != null) {
            AppStackEntry entry = new AppStackEntry(
                    currentTask.baseActivity.getPackageName(),
                    currentTask.baseActivity.getClassName(),
                    NavigationController.NavigationState.APP
            );

            // Remove duplicate if exists
            mAppStack.removeIf(e -> e.packageName.equals(entry.packageName));

            // Add to top
            mAppStack.addFirst(entry);

            // Maintain max size
            while (mAppStack.size() > MAX_STACK_SIZE) {
                mAppStack.removeLast();
            }
        }
    }

    /**
     * Navigate back to previous app
     */
    public void navigateBack(@NonNull NavigationCallback callback) {
        if (!mAppStack.isEmpty()) {
            AppStackEntry previousApp = mAppStack.removeFirst();
            launchApp(previousApp.packageName, previousApp.activityName);
        } else {
            // Go to home if stack is empty
            launchHome();
        }

        if (callback != null) {
            callback.onNavigationComplete();
        }
    }

    /**
     * Get current navigation state from top of stack
     */
    public NavigationController.NavigationState getCurrentAppState() {
        if (!mAppStack.isEmpty()) {
            return mAppStack.getFirst().state;
        }
        return NavigationController.NavigationState.HOME;
    }

    /**
     * Get all apps in stack
     */
    public List<AppStackEntry> getAppStack() {
        return new LinkedList<>(mAppStack);
    }

    /**
     * Clear app stack
     */
    public void clearStack() {
        mAppStack.clear();
    }

    /**
     * Get current running task
     */
    private ActivityManager.RunningTaskInfo getCurrentTask() {
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        return tasks.isEmpty() ? null : tasks.get(0);
    }

    /**
     * Launch an app
     */
    private void launchApp(String packageName, String activityName) {
        try {
            android.content.Intent intent = new android.content.Intent();
            intent.setClassName(packageName, activityName);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                    android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            mContext.startActivity(intent);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to launch app: " + packageName, e);
        }
    }

    /**
     * Launch home screen
     */
    private void launchHome() {
        try {
            android.content.Intent intent = new android.content.Intent(
                    android.content.Intent.ACTION_MAIN
            );
            intent.addCategory(android.content.Intent.CATEGORY_HOME);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to launch home", e);
        }
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        mAppStack.clear();
    }
}
