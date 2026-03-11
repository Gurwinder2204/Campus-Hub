# Campus Study Hub — Mobile Build Guide

The Android app now lives in `mobile/android` as a native Jetpack Compose client for:

- login and registration
- semester and subject browsing
- notes, papers, and video resources
- study planner tasks
- room booking requests

## Android

### Prerequisites

- Android SDK (API 35 installed locally is fine)
- JDK 17
- Gradle wrapper included in this repo

### Build Unsigned APK

```bash
cd mobile/android
JAVA_HOME=/path/to/jdk-17 ./gradlew assembleDebug
```

On this Windows machine the generated APK was written to:

```text
C:\Users\Gurwinder\AppData\Local\Temp\CampusHubAndroid\app\outputs\apk\debug\app-debug.apk
```

The app defaults to `http://10.0.2.2:8080/` as the backend URL for Android Emulator use. You can change the backend URL from the login screen.

### Build Signed Release APK

```bash
cd mobile/android
JAVA_HOME=/path/to/jdk-17 ./gradlew assembleRelease
```

### Using the build script

```bash
./mobile/build-android.sh
# Copies the debug APK into /builds/android/
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
