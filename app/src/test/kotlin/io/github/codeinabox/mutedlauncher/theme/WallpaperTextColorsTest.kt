package io.github.codeinabox.mutedlauncher.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperTextColorsTest {
  private val palette = PaletteTones(light = 0xFFD7E3FF.toInt(), dark = 0xFF001B3E.toInt())

  @Test
  fun darkTextHintUsesDarkTone() {
    val result = selectTextColors(supportsDarkText = true, palette = palette)
    assertEquals(palette.dark, result.textColor)
    assertTrue(result.useDarkBars)
  }

  @Test
  fun noDarkTextHintUsesLightTone() {
    val result = selectTextColors(supportsDarkText = false, palette = palette)
    assertEquals(palette.light, result.textColor)
    assertFalse(result.useDarkBars)
  }

  @Test
  fun nullHintsUseNeutralLightEvenWithPalette() {
    val result = selectTextColors(supportsDarkText = null, palette = palette)
    assertEquals(NeutralTones.light, result.textColor)
    assertFalse(result.useDarkBars)
  }

  @Test
  fun nullHintsAndNoPaletteUseNeutralLight() {
    val result = selectTextColors(supportsDarkText = null, palette = null)
    assertEquals(NeutralTones.light, result.textColor)
    assertFalse(result.useDarkBars)
  }

  @Test
  fun noPaletteWithDarkHintUsesNeutralDark() {
    val result = selectTextColors(supportsDarkText = true, palette = null)
    assertEquals(NeutralTones.dark, result.textColor)
    assertTrue(result.useDarkBars)
  }

  @Test
  fun noPaletteWithoutDarkHintUsesNeutralLight() {
    val result = selectTextColors(supportsDarkText = false, palette = null)
    assertEquals(NeutralTones.light, result.textColor)
    assertFalse(result.useDarkBars)
  }

  @Test
  fun polarityDependsOnlyOnTheWallpaperHint() {
    // selectTextColors has no system dark-mode parameter, so polarity can only come from the hint
    for (palette in listOf(palette, null)) {
      val tones = palette ?: NeutralTones
      assertTrue(selectTextColors(true, palette).useDarkBars)
      assertEquals(tones.dark, selectTextColors(true, palette).textColor)
      assertFalse(selectTextColors(false, palette).useDarkBars)
      assertEquals(tones.light, selectTextColors(false, palette).textColor)
    }
  }
}
