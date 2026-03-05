# Campus Study Hub — Mobile Build Guide

## Android

### Prerequisites

- Android SDK (API 34+)
- JDK 17
- Gradle 8+

### Build Unsigned APK

```bash
cd mobile/android
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Build Signed Release APK

```bash
# Set keystore environment variables
export ANDROID_KEYSTORE_PATH=/path/to/keystore.jks
export ANDROID_KEYSTORE_PASSWORD=your_password
export ANDROID_KEY_ALIAS=your_alias
export ANDROID_KEY_PASSWORD=your_key_password

cd mobile/android
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Using the build script

```bash
./mobile/build-android.sh
# Places APK into /builds/android/
```

---

## iOS

### Prerequisites

- macOS with Xcode 15+
- Apple Developer Account (for distribution)
- CocoaPods (`gem install cocoapods`)

### Build Steps

1. `cd mobile/ios && pod install`
2. Open `.xcworkspace` in Xcode
3. Select your team under Signing & Capabilities
4. Product → Archive
5. Distribute App → Ad Hoc / App Store Connect

### Fastlane (optional)

```bash
cd mobile/ios
bundle exec fastlane build
```

See `mobile/fastlane/Fastfile` for the lane configuration.

---

## GitHub Actions (Android CI — example)

```yaml
# .github/workflows/android-build.yml (commented example)
# name: Android Build
# on: [push]
# jobs:
#   build:
#     runs-on: ubuntu-latest
#     steps:
#       - uses: actions/checkout@v4
#       - uses: actions/setup-java@v4
#         with: { java-version: '17', distribution: temurin }
#       - run: cd mobile/android && ./gradlew assembleDebug
#       - uses: actions/upload-artifact@v4
#         with: { name: android-apk, path: 'mobile/android/app/build/outputs/apk/debug/*.apk' }
```
