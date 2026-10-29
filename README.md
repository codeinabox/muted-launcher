# Muted Launcher

A minimal Android home screen app. It shows an alphabetical, text-only list of your apps drawn directly over the system wallpaper, with text colours that follow the wallpaper.

## Features

- Alphabetical, text-only app list — no icons and no clutter
- Apps drawn directly over the system wallpaper, sorted case-insensitively with locale-aware collation
- Text colour derived from the system's wallpaper palette, with dark or light text chosen from the wallpaper's colour hints, drawn with a crisp contrasting outline for readability
- Duplicate app labels (e.g. two apps named "Camera") are told apart with a humanised package-name suffix, shown only when labels collide
- Swipe right-to-left to open the device's default camera app
- Swipe left-to-right to open the device's default contacts app

## Details

- Kotlin and Jetpack Compose, API 31+ (Android 12 and newer)

## Building

Requirements:

- JDK 17 (set `JAVA_HOME` to it)
- Android SDK (the Gradle build downloads missing platforms if the licences are accepted)

Point Gradle at your SDK, either by setting `ANDROID_HOME` or by creating `local.properties` (not committed) containing:

```
sdk.dir=/path/to/Android/sdk
```

Then build a debug APK:

```
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/`.

A release build with R8 shrinking is produced by `./gradlew assembleRelease`.

## Running tests

```
./gradlew test
```

## Licence

Apache License 2.0. See [LICENSE](LICENSE).
