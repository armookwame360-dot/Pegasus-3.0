# Pegasus 3.0 - Complete Installation & Build Guide

## 🎯 Quick Start (5 Minutes)

### Prerequisites
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK 34 (API 34)
- Minimum 4GB RAM
- 2GB free disk space

### Step 1: Clone the Repository

```bash
git clone https://github.com/armookwame360-dot/Pegasus-3.0.git
cd Pegasus-3.0
```

### Step 2: Open in Android Studio

1. Open Android Studio
2. Click **File → Open**
3. Select the `Pegasus-3.0` folder
4. Wait for Gradle sync to complete

### Step 3: Build the APK

```bash
# From command line
./gradlew assembleDebug

# Or use Android Studio:
# Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### Step 4: Install on Device

```bash
# Enable USB Debugging on device
# Then run:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 5: Run the App

```bash
# Launch on connected device
adb shell am start -n com.pegasus.system/com.pegasus.system.ui.MainActivity
```

---

## 🔧 Full Build Instructions

### Using Gradle (Recommended)

```bash
# Navigate to project directory
cd Pegasus-3.0

# Build debug APK
./gradlew assembleDebug

# Build release APK (production)
./gradlew assembleRelease

# Build and run on connected device
./gradlew installDebug
./gradlew runDebug
```

### Using Android Studio GUI

1. **Build Menu** → **Build APK(s)**
2. Wait for build to complete (2-5 minutes)
3. Click **Locate** to find APK file
4. APK saved to: `android/app/build/outputs/apk/`

### Project Structure

```
Pegasus-3.0/
├── android/
│   └── app/
│       ├── src/main/
│       │   ├── java/com/pegasus/
│       │   │   ├── ui/
│       │   │   │   ├── MainActivity.java
│       │   │   │   ├── settings/
│       │   │   │   ├── privacy/
│       │   │   │   └── permissions/
│       │   │   ├── services/
│       │   │   │   ├── BootInstallerService.java
│       │   │   │   └── PrivacyMonitorService.java
│       │   │   └── receivers/
│       │   │       └── BootReceiver.java
│       │   ├── res/
│       │   │   ├── layout/ (XML layouts)
│       │   │   ├── values/ (strings, colors)
│       │   │   └── drawable/ (graphics)
│       │   └── AndroidManifest.xml
│       └── build.gradle
├── ui/
│   ├── navigation/src/
│   ├── glass-morphism/src/
│   └── icon-pack/src/
├── security/src/
└── docs/
```

---

## 📱 Installation Methods

### Method 1: ADB Installation (Easiest)

```bash
# 1. Enable USB Debugging on Android device
# 2. Connect device to computer
# 3. Verify device is connected
adb devices

# 4. Install the APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 5. Launch the app
adb shell am start -n com.pegasus.system/com.pegasus.system.ui.MainActivity
```

### Method 2: Direct APK Installation

1. Build APK as described above
2. Transfer APK to Android device
3. Open file manager on device
4. Locate and tap the APK file
5. Grant permissions when prompted
6. Tap **Install**
7. Tap **Open** to launch

### Method 3: Android Studio Device Manager

1. Connect device via USB
2. In Android Studio, go to **Run → Run 'app'**
3. Select device from list
4. Click **OK**
5. App will build, install, and run automatically

---

## ⚙️ System Requirements

### Device Requirements
- **Android Version**: 13 (API 33) or higher
- **RAM**: 6GB minimum (8GB recommended)
- **Storage**: 500MB free space
- **Processor**: ARM64 or x86_64

### Development Environment
- **OS**: Windows, macOS, or Linux
- **Android Studio**: 2022.1 or later
- **Java**: JDK 11 or JDK 17
- **SDK**: Android SDK 34 (installed via SDK Manager)
- **Gradle**: 7.5 or later (auto-managed by project)

---

## 🚀 First Run Boot Sequence

When you first launch Pegasus 3.0, the app automatically performs the following initialization:

```
========== PEGASUS 3.0 BOOT INSTALLATION ==========
[1/6] Initializing Navigation System...
[2/6] Initializing Security Framework...
[3/6] Starting Privacy Monitor...
[4/6] Loading Glass Morphism Effects...
[5/6] Loading Custom Icon Pack...
[6/6] Applying System Policies...
========== PEGASUS 3.0 BOOT COMPLETE ==========
```

**All this happens automatically - no user action required!**

---

## 📊 Features After Installation

### ✅ Navigation System
- Gesture-based navigation (swipe back, home, recents)
- Background app stack management
- Smooth 60 FPS animations
- Dynamic navigation bar

### ✅ Glass Morphism UI
- Liquid glass effects throughout the app
- Blur effects with GPU acceleration
- Smooth transitions and animations
- Material design compliance

### ✅ Security & Privacy
- Real-time permission monitoring
- Privacy dashboard showing app access
- Granular permission control
- Complete audit logs

### ✅ Custom Icons
- 128+ custom designed icons
- Themed icon pack
- Efficient caching system
- Fast app launches

---

## 🐛 Troubleshooting

### Build Fails: "SDK not found"

```bash
# Open SDK Manager and install:
# - Android SDK 34 (API Level 34)
# - Android SDK Build-Tools 34.x.x
# - Android Emulator (optional)
```

### Gradle Sync Error

```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### APK Won't Install

```bash
# Uninstall previous version first
adb uninstall com.pegasus.system

# Then install again
adb install app/build/outputs/apk/debug/app-debug.apk
```

### App Crashes on Startup

1. Check logcat output:
```bash
adb logcat | grep PegasusMain
```

2. Look for permission errors:
```bash
adb logcat | grep "Permission"
```

3. Verify device is running Android 13+:
```bash
adb shell getprop ro.build.version.release
```

### Device Not Detected

```bash
# Restart ADB server
adb kill-server
adb start-server

# Check device connection
adb devices

# Enable USB debugging on device
# Settings → Developer Options → USB Debugging → ON
```

---

## 📝 Development Commands

```bash
# Build
./gradlew build
./gradlew assembleDebug
./gradlew assembleRelease

# Install & Run
./gradlew installDebug
./gradlew runDebug

# Testing
./gradlew test
./gradlew connectedAndroidTest

# Clean
./gradlew clean
./gradlew cleanBuild

# View dependencies
./gradlew dependencies

# Generate APK report
./gradlew bundleRelease
```

---

## 🔒 Release Build (For Distribution)

### Step 1: Create Signing Key

```bash
keytool -genkey -v -keystore pegasus-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias pegasus_key
```

### Step 2: Configure Signing

Add to `android/app/build.gradle`:

```gradle
signingConfigs {
    release {
        storeFile file('../../pegasus-release.jks')
        storePassword 'your_store_password'
        keyAlias 'pegasus_key'
        keyPassword 'your_key_password'
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
    }
}
```

### Step 3: Build Release APK

```bash
./gradlew assembleRelease
```

Release APK saved to: `android/app/build/outputs/apk/release/`

---

## 📚 Additional Resources

- [Android Developer Guide](https://developer.android.com/)
- [Android Studio Documentation](https://developer.android.com/studio/intro)
- [Gradle Build System](https://gradle.org/)
- [Android API Reference](https://developer.android.com/reference)

---

## ✅ Verification Checklist

- [ ] Java 11+ installed (`java -version`)
- [ ] Android SDK 34 installed
- [ ] Project synced in Android Studio
- [ ] No Gradle errors in build
- [ ] APK built successfully
- [ ] Device connected and detected (`adb devices`)
- [ ] USB Debugging enabled on device
- [ ] App installed successfully
- [ ] App launches without crashes
- [ ] All 6 boot services initialized

---

## 🎉 Success!

If you've completed all steps, Pegasus 3.0 is now installed and running on your device!

### Next Steps:
1. Explore the app's features
2. Check Settings for customization options
3. Monitor Privacy Dashboard for insights
4. Review Permissions to understand app access
5. Report any issues on GitHub

---

**Enjoy Pegasus 3.0!** 🚀

*For support, issues, or contributions, visit: https://github.com/armookwame360-dot/Pegasus-3.0*
