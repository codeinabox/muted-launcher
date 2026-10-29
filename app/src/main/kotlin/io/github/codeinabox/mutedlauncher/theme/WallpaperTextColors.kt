package io.github.codeinabox.mutedlauncher.theme

data class PaletteTones(val light: Int, val dark: Int)

data class TextColors(val textColor: Int, val useDarkBars: Boolean)

val NeutralTones = PaletteTones(light = 0xFFF2F2F2.toInt(), dark = 0xFF1A1A1A.toInt())

/**
 * @param supportsDarkText the wallpaper's dark-text hint, or null when the system reports no
 *   wallpaper colours
 * @param palette accent tones from the system palette, or null when unavailable
 */
fun selectTextColors(supportsDarkText: Boolean?, palette: PaletteTones?): TextColors {
  if (supportsDarkText == null) {
    return TextColors(NeutralTones.light, useDarkBars = false)
  }
  val tones = palette ?: NeutralTones
  return if (supportsDarkText) {
    TextColors(tones.dark, useDarkBars = true)
  } else {
    TextColors(tones.light, useDarkBars = false)
  }
}
