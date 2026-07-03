# Plate Quest

Plate Quest is a local-only Android scavenger game for tracking license-plate characters by position.

Example: entering `TGL-7815` records:

- `T` in position 1
- `G` in position 2
- `L` in position 3
- `7` in position 4
- `8` in position 5
- `1` in position 6
- `5` in position 7

The first three positions maintain their own A-Z and 0-9 collections. The last four positions track 0-9 only.

## Included features

- Seven-character manual plate entry
- Automatic `XXX-XXXX` display formatting
- A-Z and 0-9 tracking for the first three positions
- 0-9 tracking for the last four positions
- Overall and per-position completion statistics
- Recent plate history
- Local storage with `SharedPreferences`
- No account, internet permission, camera permission, or location permission
- Full reset option
- Passenger-mode safety reminder

## Build an APK in Android Studio

1. Install Android Studio.
2. Open the `PlateQuest` folder as a project.
3. Allow Android Studio to install Android SDK 36 if prompted.
4. Wait for Gradle sync to complete.
5. Select **Build > Build APK(s)**.
6. Android Studio will place the debug APK under `app/build/outputs/apk/debug/`.

The included wrapper is configured for Gradle 8.13 and the app uses Android Gradle Plugin 8.13.2 with JDK 17.

## Command-line build

With Android SDK 36 installed and `ANDROID_HOME` configured:

```bash
./gradlew assembleDebug
```

The generated APK will be:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Privacy

Plate data remains on the device. This version stores the eight most recently entered plates to provide game history. It does not transmit data anywhere.

## Safety

Do not interact with the app while driving. A passenger should enter plates, or the driver should wait until safely parked.
