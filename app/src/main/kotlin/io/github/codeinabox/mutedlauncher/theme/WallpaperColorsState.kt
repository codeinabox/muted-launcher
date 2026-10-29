package io.github.codeinabox.mutedlauncher.theme

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

private fun WallpaperColors?.supportsDarkText(): Boolean? =
    this?.let { (it.colorHints and WallpaperColors.HINT_SUPPORTS_DARK_TEXT) != 0 }

private fun Context.systemPalette(): PaletteTones? =
    try {
      PaletteTones(
          light = getColor(android.R.color.system_accent1_100),
          dark = getColor(android.R.color.system_accent1_900),
      )
    } catch (_: Resources.NotFoundException) {
      null
    }

@Composable
fun rememberWallpaperTextColors(): State<TextColors> {
  val context = LocalContext.current
  val wallpaperManager = remember(context) { WallpaperManager.getInstance(context) }
  var supportsDarkText by remember {
    mutableStateOf(
        wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM).supportsDarkText()
    )
  }
  var palette by remember { mutableStateOf(context.systemPalette()) }

  DisposableEffect(wallpaperManager, context) {
    val listener = WallpaperManager.OnColorsChangedListener { colors, which ->
      if (which and WallpaperManager.FLAG_SYSTEM != 0) {
        supportsDarkText = colors.supportsDarkText()
        palette = context.systemPalette()
      }
    }
    wallpaperManager.addOnColorsChangedListener(listener, Handler(Looper.getMainLooper()))
    onDispose { wallpaperManager.removeOnColorsChangedListener(listener) }
  }

  return remember { derivedStateOf { selectTextColors(supportsDarkText, palette) } }
}
