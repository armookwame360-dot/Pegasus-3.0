# Pegasus 3.0 - Build & Configuration Guide

## Build System

Pegasus 3.0 uses the standard Android build system with custom extensions.

### Build Files

```
build/
├── build.sh           # Main build script
├── config.mk          # Build configuration
├── install-deps.sh    # Dependency installer
└── apply-patches.sh   # AOSP patch applicator
```

## Building Pegasus 3.0

### Prerequisites

```bash
# Install dependencies (Ubuntu/Debian)
sudo apt-get install git-core gnupg flex bison build-essential \
    zip curl zlib1g-dev gcc-multilib g++-multilib libc6-dev-i386 \
    lib32ncurses5-dev x11-utils libssl-dev libxml2-utils yamllint

# Install Java
sudo apt-get install openjdk-11-jdk
```

### Full Build Process

```bash
# 1. Clone repository
git clone https://github.com/armookwame360-dot/Pegasus-3.0.git
cd Pegasus-3.0

# 2. Initialize AOSP
repo init -u https://android.googlesource.com/platform/manifest -b android-13

# 3. Apply Pegasus patches
./build/apply-patches.sh

# 4. Source build environment
source build/envsetup.sh

# 5. Select target device
lunch pegasus_3_0-userdebug

# 6. Build
make -j$(nproc)
```

### Output

Build artifacts are in `out/target/product/`:
- `system.img` - System partition
- `boot.img` - Boot partition
- `recovery.img` - Recovery partition
- `pegasus_3_0-ota-*.zip` - OTA update package

## Device Flashing

### Using ADB/Fastboot

```bash
# Boot device to fastboot
adb reboot bootloader

# Flash images
fastboot flash system out/target/product/pegasus_3_0/system.img
fastboot flash boot out/target/product/pegasus_3_0/boot.img
fastboot flash recovery out/target/product/pegasus_3_0/recovery.img

# Reboot
fastboot reboot
```

### Using TWRP Recovery

```bash
# Transfer OTA package
adb push out/target/product/pegasus_3_0/pegasus_3_0-ota-*.zip /sdcard/

# Reboot to recovery
adb reboot recovery

# Flash via TWRP UI
```

## Supported Devices

- Pixel 6/6 Pro
- Pixel 5/5a
- OnePlus 9/9 Pro
- Samsung Galaxy S21 series
- Xiaomi Mi 11

*More devices coming soon*

## Troubleshooting

### Build Fails

```bash
# Clean build
make clean
make clobber

# Rebuild
make -j$(nproc)
```

### Out of Memory

```bash
# Reduce parallel jobs
make -j4
```

### Device Not Detected

```bash
# Check connection
adb devices

# Restart adb
adb kill-server
adb start-server
```

## Environment Variables

```bash
# Build type
export BUILD_TYPE=userdebug  # userdebug, user, eng

# Parallel jobs
export MAKEFLAGS=-j8

# Custom out directory
export OUT_DIR=out
```

## Performance Tuning

### Faster Builds

```bash
# Use ccache for C++ compilation
export USE_CCACHE=1
export CCACHE_DIR=/path/to/ccache
ccache -M 100G

# Skip certain modules
make -j$(nproc) skip_post_static_checks=true
```

## Additional Resources

- [AOSP Build Guide](https://source.android.com/setup/build/building)
- [Device-Specific Guides](https://source.android.com/setup/build/devices)
- [Build System Documentation](https://source.android.com/setup/build)

---

*Happy building!* 🚀
