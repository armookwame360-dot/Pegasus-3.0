package com.pegasus.system.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pegasus.system.services.BootInstallerService;

/**
 * BootReceiver - Automatically starts Pegasus system on device boot
 * Ensures all components are initialized on every device startup
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        if (action == null) return;

        // Start on boot completed
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) ||
            action.equals("com.htc.intent.action.QUICKBOOT_POWERON")) {

            android.util.Log.d(TAG, "Device boot detected, starting Pegasus system...");

            // Start boot installer service
            Intent bootIntent = new Intent(context, BootInstallerService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(bootIntent);
            } else {
                context.startService(bootIntent);
            }
        }
    }
}
