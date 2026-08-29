# Pegasus 3.0 Development Guide

## Development Setup

### System Requirements

- **OS**: Ubuntu 20.04 LTS or later
- **RAM**: Minimum 16GB (32GB recommended)
- **Storage**: 200GB+ free space
- **Build Tools**: Git, Repo, Make, Python 3.8+

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/armookwame360-dot/Pegasus-3.0.git
   cd Pegasus-3.0
   ```

2. **Install dependencies**
   ```bash
   ./build/install-dependencies.sh
   ```

3. **Initialize AOSP sources**
   ```bash
   repo init -u https://android.googlesource.com/platform/manifest -b android-13
   ```

4. **Apply Pegasus patches**
   ```bash
   ./build/apply-patches.sh
   ```

## Project Structure Overview

### `/android/frameworks/`
Contains modifications to the Android framework:
- SystemUI customizations
- Window management for liquid glass effects
- Navigation bar enhancements

### `/ui/`
All UI/UX components:
- `navigation/` - Gesture navigation and navigation bar
- `glass-morphism/` - Liquid glass visual effects
- `icon-pack/` - Custom styled app icons

### `/security/`
Security enhancements:
- Permission framework modifications
- App sandboxing improvements
- Encryption utilities

### `/kernel/`
Kernel-level optimizations:
- Custom patches for performance
- Power management tweaks
- Memory optimization

## Building Pegasus 3.0

### Full Build

```bash
cd Pegasus-3.0
source build/build.sh
lunch pegasus_3_0-userdebug
make -j$(nproc)
```

### Incremental Build

```bash
make -j$(nproc) # Build only changed files
```

### Specific Module

```bash
make module_name
```

## Development Workflow

### 1. Creating a Branch

```bash
git checkout -b feature/your-feature-name
```

### 2. Making Changes

- Follow Android coding standards
- Document your changes
- Test thoroughly

### 3. Committing

```bash
git add .
git commit -m "feature: description of your changes"
```

### 4. Pushing & Creating PR

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub.

## Key Development Areas

### Navigation System (`/ui/navigation/`)

The navigation bar is core to the Pegasus experience:
- Gesture detection and handling
- Navigation state management
- Background app navigation support

**Main Files**:
- `NavigationController.java` - Core navigation logic
- `GestureRecognizer.java` - Touch gesture detection
- `NavigationBar.java` - UI rendering

### Liquid Glass Effects (`/ui/glass-morphism/`)

Implements smooth glass morphism effects:
- Blur effects
- Transparency blending
- Smooth animations

**Main Files**:
- `GlassEffect.java` - Core glass effect engine
- `BlurRenderer.java` - Blur rendering pipeline
- `TransitionAnimator.java` - Smooth transitions

### Security Framework (`/security/`)

Enhanced permission and security controls:
- Permission auditing
- Real-time privacy monitoring
- Sandboxing improvements

**Main Files**:
- `PermissionManager.java` - Permission handling
- `SecurityMonitor.java` - Runtime monitoring
- `SandboxPolicy.java` - Sandboxing rules

## Testing

### Unit Tests
```bash
./build/run-tests.sh unit
```

### Integration Tests
```bash
./build/run-tests.sh integration
```

### On Device Testing
```bash
adb install-multiple $(find out/target -name "*.apk")
```

## Debugging

### Enable Logging
```bash
adb shell setprop persist.sys.pegasus.debug 1
```

### View Logs
```bash
adb logcat | grep Pegasus
```

### Debug Build
```bash
lunch pegasus_3_0-eng
make -j$(nproc)
```

## Performance Profiling

### CPU Profiling
```bash
simpleperf record -a -g
simpleperf report
```

### Memory Profiling
```bash
adb shell meminfo
```

## Code Style Guidelines

- Follow Google's Java Style Guide
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and small
- Maximum line length: 100 characters

## Common Issues & Solutions

### Build Fails
```bash
make clean
make -j$(nproc)
```

### Out of Memory
Reduce parallel jobs:
```bash
make -j4
```

### Device Connection Issues
```bash
adb kill-server
adb start-server
adb devices
```

## Resources

- [AOSP Documentation](https://source.android.com/)
- [Android Framework Guide](https://developer.android.com/guide)
- [Git Workflow Guide](https://git-scm.com/book)

## Getting Help

- Check existing GitHub Issues
- Review AOSP documentation
- Ask in Pull Request discussions
- Contact maintainers

---

Happy coding! 🚀
