#!/bin/sh
set -e
cd "$(dirname "$0")"
./gradlew assembleDebug
echo "APK created at app/build/outputs/apk/debug/app-debug.apk"
