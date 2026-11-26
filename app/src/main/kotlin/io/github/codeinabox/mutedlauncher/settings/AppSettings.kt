package io.github.codeinabox.mutedlauncher.settings

/** Horizontal text alignment of app-list entries. */
enum class ListAlignment {
  Left,
  Center,
  Right,
}

/** Font size of app-list entry text. */
enum class ListFontSize {
  Small,
  Medium,
  Large,
}

/** All persisted launcher settings, with defaults matching the unset state. */
data class AppSettings(
    val alignment: ListAlignment = ListAlignment.Left,
    val outlineEnabled: Boolean = true,
    val fontSize: ListFontSize = ListFontSize.Medium,
)
