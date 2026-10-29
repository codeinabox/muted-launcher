package io.github.codeinabox.mutedlauncher.ui

import android.content.ComponentName
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import io.github.codeinabox.mutedlauncher.apps.AppEntry
import io.github.codeinabox.mutedlauncher.theme.TextColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppListDisplayTest {
  private fun entry(label: String, disambiguator: String? = null) =
      AppEntry(label, ComponentName("pkg", "Main"), disambiguator)

  @Test
  fun bareLabelWhenNoDisambiguator() {
    assertEquals("Clock", displayText(entry("Clock")))
  }

  @Test
  fun suffixesDisambiguatorInParentheses() {
    assertEquals("Camera (Google Camera)", displayText(entry("Camera", "Google Camera")))
  }

  @Test
  fun entryTextStyleHasNoShadow() {
    val style = entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false))
    assertNull(style.shadow)
  }

  @Test
  fun outlineTextStyleUsesStrokeAndNoShadow() {
    val fillStyle = entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false))
    val outline = outlineTextStyle(fillStyle, Color.Black, Density(1f))
    assertNull(outline.shadow)
    assertEquals(Color.Black, outline.color)
    assertTrue(outline.drawStyle is Stroke)
    assertEquals(1.5f, (outline.drawStyle as Stroke).width)
  }
}
