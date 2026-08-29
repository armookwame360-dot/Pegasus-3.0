# Pegasus 3.0 Installation Guide

## System Requirements

### Device Requirements
- **Android Version**: 13 (API 33) or higher
- **RAM**: Minimum 6GB (8GB recommended)
- **Storage**: 8GB free space minimum
- **Processor**: ARM64 or x86_64
- **Bootloader**: Must be unlockable

### Development Environment
- **OS**: Linux (Ubuntu 20.04 LTS preferred), macOS, or Windows WSL2
- **RAM**: 16GB minimum (32GB recommended for full builds)
- **Storage**: 200GB+ free space
- **Tools**: Git, Repo, ADB, Fastboot, JDK 11+

## Pre-Installation Setup

### 1. Unlock Bootloader

**WARNING**: This will wipe all data on your device!

```bash
# Reboot to bootloader
adb reboot bootloader

# Unlock bootloader (device-specific)
fastboot flashing unlock
# Or
fastboot oem unlock

# Confirm on device
```

### 2. Install ADB & Fastboot

**Ubuntu/Debian**:
```bash
sudo apt-get update
sudo apt-get install android-tools-adb android-tools-fastboot
```

**macOS**:
```bash
brew install android-platform-tools
```

**Windows**:
- Download from [Google Android SDK Platform Tools](https://developer.android.com/studio/releases/platform-tools)

### 3. Enable USB Debugging

1. Go to **Settings** → **About Phone**
2. Tap **Build Number** 7 times to enable Developer Mode
3. Go to **Settings** → **Developer Options**
4. Enable **USB Debugging**

## Installation Methods

### Method 1: Using Official Flashable ZIP (Recommended)

```bash
# 1. Download Pegasus OTA package
wget https://github.com/armookwame360-dot/Pegasus-3.0/releases/download/v1.0/pegasus_3_0-ota-arm64-v8a.zip

# 2. Boot to TWRP Recovery
adb reboot recovery

# 3. In TWRP:
# - Select "Install"
# - Navigate to downloaded ZIP
# - Swipe to confirm flash
# - Reboot system
```

### Method 2: Using Fastboot (Advanced)

```bash
# 1. Download all images
wget https://github.com/armookwame360-dot/Pegasus-3.0/releases/download/v1.0/pegasus-images.tar.gz
tar -xzf pegasus-images.tar.gz

# 2. Boot to fastboot
adb reboot bootloader

# 3. Flash images
fastboot flash system pegasus_3_0-system.img
fastboot flash boot pegasus_3_0-boot.img
fastboot flash recovery pegasus_3_0-recovery.img
fastboot flash vendor pegasus_3_0-vendor.img
fastboot flash product pegasus_3_0-product.img

# 4. Reboot
fastboot reboot
```

### Method 3: Building from Source

```bash
# 1. Clone repository
git clone https://github.com/armookwame360-dot/Pegasus-3.0.git
cd Pegasus-3.0

# 2. Install dependencies
sudo bash ./build/install-dependencies.sh

# 3. Initialize AOSP
repo init -u https://android.googlesource.com/platform/manifest -b android-13

# 4. Download sources
repo sync -j$(nproc)

# 5. Apply Pegasus patches
bash ./build/apply-patches.sh

# 6. Build
source build/envsetup.sh
lunch pegasus_3_0-userdebug
make -j$(nproc)

# 7. Flash
adb reboot bootloader
fastboot flash system out/target/product/pegasus_3_0/system.img
fastboot flash boot out/target/product/pegasus_3_0/boot.img
fastboot flash recovery out/target/product/pegasus_3_0/recovery.img
fastboot reboot
```

## Post-Installation Setup

### 1. Initial Boot

- First boot may take 5-10 minutes
- System will optimize apps (normal behavior)
- Do NOT force shutdown during this process

### 2. Complete Setup Wizard

1. Select Language
2. Connect to WiFi
3. Sign in to Google Account (optional)
4. Set up security (PIN, pattern, or biometric)
5. Review privacy settings

### 3. Verify Installation

```bash
# Check system info
adb shell getprop ro.system.build.fingerprint

# Should show: pegasus/3.0

# Test navigation
# - Swipe from left edge for back
# - Swipe up from bottom for home
# - Swipe from right edge for recents
```

## Features After Installation

✅ **Navigation System**
- Smooth gesture-based navigation
- Background app switching
- Dynamic navigation bar

✅ **Liquid Glass UI**
- Frosted glass effect throughout UI
- Smooth animations and transitions
- Theme customization

✅ **Enhanced Security**
- Advanced permission controls
- Real-time privacy monitoring
- Sandboxing improvements
- Granular app permissions

✅ **Styled Icons**
- Custom icon pack
- Consistent design language
- Theme support

## Troubleshooting

### Device Won't Boot

```bash
# Boot to recovery
adb reboot recovery

# Factory reset (in TWRP)
# - Wipe → Format Data
# - Mount → Mount System
# - Reboot to bootloader
# - Flash again
```

### Bootloop

```bash
# Hold power + volume down to force boot menu
# Select Recovery
# Wipe cache partition
# Reboot system
```

### ADB Not Recognizing Device

```bash
# Restart ADB daemon
adb kill-server
adb start-server

# Check connection
adb devices

# On device: Allow USB debugging
```

### Performance Issues

1. Clear cache: **Settings → Storage → Clear Cache**
2. Restart device
3. Disable unnecessary animations: **Settings → Display → Animation Scale → 0.5x**
4. Close background apps

### Reverting to Stock

```bash
# Flash stock ROM using your device's recovery method
# See manufacturer's instructions for your specific device
```

## Supported Devices

### Officially Supported
- ✅ Pixel 6/6 Pro
- ✅ Pixel 5/5a
- ✅ OnePlus 9/9 Pro
- ✅ Samsung Galaxy S21 series
- ✅ Xiaomi Mi 11

### Community Supported
- OnePlus 8/8 Pro (limited testing)
- Pixel 4/4 XL (legacy support)
- Various Qualcomm Snapdragon devices

## Getting Help

1. **GitHub Issues**: https://github.com/armookwame360-dot/Pegasus-3.0/issues
2. **Documentation**: Check [docs/](docs/) folder
3. **Build Issues**: See [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)
4. **Device-Specific**: Check device-specific guides in [docs/](docs/)

## Safety Notice

⚠️ **Important**:
- Always backup your data before flashing
- Unlocking bootloader voids warranty (check manufacturer)
- Installation at your own risk
- Pegasus 3.0 team not responsible for device damage
- Keep recovery image for safety

## Next Steps

After successful installation:

1. **Explore Settings** → Customize Pegasus-specific options
2. **Test Navigation** → Try all gesture controls
3. **Check Privacy Settings** → Review app permissions
4. **Customize Icons** → Apply different icon themes
5. **Report Bugs** → Help improve Pegasus!

---

**Enjoy Pegasus 3.0!** 🚀
