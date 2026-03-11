#!/bin/sh
# ============================================================
# Campus Study Hub — Android APK Build Script
# ============================================================

set -e

BUILD_DIR="$(cd "$(dirname "$0")/.." && pwd)/builds/android"
ANDROID_DIR="$(cd "$(dirname "$0")" && pwd)/android"
TMP_ROOT="${TMPDIR:-${TEMP:-/tmp}}"

echo "=== Campus Study Hub — Android Build ==="

# Create output directory
mkdir -p "$BUILD_DIR"

# Check if Android project exists
if [ ! -d "$ANDROID_DIR" ]; then
  echo "ERROR: Android project not found at $ANDROID_DIR"
  echo "Please initialize the React Native / Android project first."
  exit 1
fi

cd "$ANDROID_DIR"

# Build debug APK (unsigned)
echo "Building debug APK..."
./gradlew assembleDebug

# Copy APK to builds directory
APK_PATH="$TMP_ROOT/CampusHubAndroid/app/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
  cp "$APK_PATH" "$BUILD_DIR/campus-study-hub-debug.apk"
  echo "✅ APK built successfully: $BUILD_DIR/campus-study-hub-debug.apk"
else
  echo "❌ APK not found at $APK_PATH"
  exit 1
fi

echo "=== Build Complete ==="
