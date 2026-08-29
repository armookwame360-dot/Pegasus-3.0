package com.pegasus.system.ui;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.pegasus.system.R;
import com.pegasus.system.services.BootInstallerService;

/**
 * MainActivity - Entry point for Pegasus 3.0 system
 * Initializes all system components on first launch
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PegasusMain";
    private Button mSettingsButton;
    private Button mPrivacyButton;
    private Button mPermissionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize system on first launch
        initializeSystem();

        // Setup UI
        setupUI();
    }

    /**
     * Initialize Pegasus 3.0 system components
     */
    private void initializeSystem() {
        // Start boot installer service
        Intent bootIntent = new Intent(this, BootInstallerService.class);
        startService(bootIntent);

        // Initialize navigation system
        initializeNavigationSystem();

        // Initialize security system
        initializeSecuritySystem();

        // Initialize glass effects
        initializeGlassEffects();

        android.util.Log.d(TAG, "Pegasus 3.0 System Initialized");
    }

    /**
     * Setup navigation system
     */
    private void initializeNavigationSystem() {
        try {
            com.pegasus.ui.navigation.NavigationController navController =
                    new com.pegasus.ui.navigation.NavigationController(this);
            android.util.Log.d(TAG, "Navigation System Started");
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to initialize navigation", e);
        }
    }

    /**
     * Setup security system
     */
    private void initializeSecuritySystem() {
        try {
            com.pegasus.security.PermissionManager permManager =
                    new com.pegasus.security.PermissionManager(this);
            android.util.Log.d(TAG, "Security System Started");
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to initialize security", e);
        }
    }

    /**
     * Setup glass effect system
     */
    private void initializeGlassEffects() {
        try {
            // Glass effects will be applied via theme
            android.util.Log.d(TAG, "Glass Effects System Started");
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to initialize glass effects", e);
        }
    }

    /**
     * Setup UI components
     */
    private void setupUI() {
        mSettingsButton = findViewById(R.id.btn_settings);
        mPrivacyButton = findViewById(R.id.btn_privacy);
        mPermissionsButton = findViewById(R.id.btn_permissions);

        mSettingsButton.setOnClickListener(v -> openSettings());
        mPrivacyButton.setOnClickListener(v -> openPrivacyDashboard());
        mPermissionsButton.setOnClickListener(v -> openPermissionsManager());
    }

    /**
     * Open settings activity
     */
    private void openSettings() {
        Intent intent = new Intent(this, com.pegasus.system.ui.settings.SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Open privacy dashboard
     */
    private void openPrivacyDashboard() {
        Intent intent = new Intent(this, com.pegasus.system.ui.privacy.PrivacyDashboardActivity.class);
        startActivity(intent);
    }

    /**
     * Open permissions manager
     */
    private void openPermissionsManager() {
        Intent intent = new Intent(this, com.pegasus.system.ui.permissions.PermissionManagerActivity.class);
        startActivity(intent);
    }
}
