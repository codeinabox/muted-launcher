# Muted Launcher

[<img src="badge_obtainium.png" alt="Get it on Obtainium" width="161">](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%20%22io.github.codeinabox.mutedlauncher%22%2C%20%22url%22%3A%20%22https%3A%2F%2Fgithub.com%2Fcodeinabox%2Fmuted-launcher%22%2C%20%22author%22%3A%20%22codeinabox%22%2C%20%22name%22%3A%20%22Muted%20Launcher%22%7D)

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

- JDK 21 (auto-provisioned by Gradle on first run if absent)
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
