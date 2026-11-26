package io.github.codeinabox.mutedlauncher.ui

import android.content.ComponentName
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import io.github.codeinabox.mutedlauncher.apps.AppEntry
import io.github.codeinabox.mutedlauncher.settings.AppSettings
import io.github.codeinabox.mutedlauncher.settings.ListAlignment
import io.github.codeinabox.mutedlauncher.settings.ListFontSize
import io.github.codeinabox.mutedlauncher.theme.TextColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
  fun defaultAlignmentIsLeft() {
    val style = entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false))

    assertEquals(TextAlign.Left, style.textAlign)
    assertEquals(Alignment.CenterStart, boxAlignmentFor(ListAlignment.Left))
  }

  @Test
  fun centerAlignmentCentersTextAndRow() {
    val style =
        entryTextStyle(
            TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false),
            ListAlignment.Center,
        )

    assertEquals(TextAlign.Center, style.textAlign)
    assertEquals(Alignment.Center, boxAlignmentFor(ListAlignment.Center))
  }

  @Test
  fun rightAlignmentEndsTextAndRow() {
    val style =
        entryTextStyle(
            TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false),
            ListAlignment.Right,
        )

    assertEquals(TextAlign.Right, style.textAlign)
    assertEquals(Alignment.CenterEnd, boxAlignmentFor(ListAlignment.Right))
  }

  @Test
  fun allEntriesShareConfiguredAlignment() {
    // The list composable computes the style and row-box alignment once and applies
    // them to every row, so each ListAlignment must map consistently across calls.
    ListAlignment.entries.forEach { alignment ->
      val style =
          entryTextStyle(
              TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false),
              alignment,
          )

      assertEquals(textAlignFor(alignment), style.textAlign)
      assertEquals(boxAlignmentFor(alignment), boxAlignmentFor(alignment))
    }
  }

  @Test
  fun settingsEntryIsFinalRowAfterAllApps() {
    val rows =
        appListRows(listOf(entry("Clock"), entry("Camera", "Google Camera")), listLoaded = true)

    assertEquals("Clock", rows[0].text)
    assertEquals("Camera (Google Camera)", rows[1].text)
    assertEquals(SETTINGS_ROW_KEY, rows.last().key)
    assertNull(rows.last().app)
  }

  @Test
  fun settingsEntryAbsentBeforeListLoads() {
    // Until the first load completes the settings row is withheld, so it cannot render
    // alone at the top of an otherwise empty list on first launch.
    val rows = appListRows(emptyList(), listLoaded = false)

    assertTrue(rows.isEmpty())
  }

  @Test
  fun settingsEntryAppearsOnceListLoads() {
    val rowsBefore = appListRows(emptyList(), listLoaded = false)
    val rowsAfter = appListRows(emptyList(), listLoaded = true)

    assertTrue(rowsBefore.none { isSettingsRow(it) })
    assertEquals(SETTINGS_ROW_KEY, rowsAfter.single().key)
    assertNull(rowsAfter.single().app)
  }

  @Test
  fun settingsEntryAfterAppsOnlyOnceLoaded() {
    val rows = appListRows(listOf(entry("Clock")), listLoaded = false)

    assertEquals(1, rows.size)
    assertFalse(isSettingsRow(rows.single()))
  }

  @Test
  fun settingsEntryUsesAppRowStyling() {
    // Every row — app entries and the settings entry — shares the same single-line
    // OutlinedText treatment driven by entryTextStyle; the settings row adds no
    // separate style, so its text simply equals the app-row display path's label.
    val textColors = TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false)

    assertEquals(SETTINGS_ROW_LABEL, appListRows(emptyList(), listLoaded = true).single().text)
    assertEquals(
        displayText(entry("Clock")),
        appListRows(listOf(entry("Clock")), listLoaded = true).first().text,
    )
    assertEquals(
        entryTextStyle(textColors, ListAlignment.Right),
        entryTextStyle(textColors, ListAlignment.Right),
    )
  }

  @Test
  fun tappingSettingsEntryOpensSettingsNotAppLaunch() {
    val rows =
        appListRows(listOf(entry("Clock"), entry("Camera", "Google Camera")), listLoaded = true)

    assertFalse(isSettingsRow(rows[0]))
    assertFalse(isSettingsRow(rows[1]))
    assertTrue(isSettingsRow(rows.last()))
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

  @Test
  fun defaultSettingsShowOutlinedEntries() {
    // Default AppSettings matches the unset state: every entry drawn with an outline.
    assertTrue(AppSettings().outlineEnabled)
  }

  @Test
  fun outlineOffRendersPlainStyleForEveryEntry() {
    // The composable renders every row through the single EntryText call site; with the
    // outline off the rendered style is the plain fill style — no Stroke drawStyle — and
    // the flag is list-level, so app rows and the settings row cannot diverge.
    val style = entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false))

    assertNull(style.drawStyle)
    assertFalse(AppSettings(outlineEnabled = false).outlineEnabled)
    assertEquals(
        entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false)),
        style,
    )
  }

  @Test
  fun outlineBackOnRendersOutlinedStyleForEveryEntry() {
    // Turning the outline back on restores the outlined treatment: the outline style is
    // derived from the same fill style for every row.
    val fillStyle = entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false))
    val outline = outlineTextStyle(fillStyle, Color.White, Density(1f))

    assertTrue(AppSettings(outlineEnabled = true).outlineEnabled)
    assertTrue(outline.drawStyle is Stroke)
    assertEquals(fillStyle.fontSize, outline.fontSize)
    assertEquals(fillStyle.textAlign, outline.textAlign)
  }

  @Test
  fun defaultFontSizeIsMedium() {
    val style = entryTextStyle(TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false))

    assertEquals(22f, style.fontSize.value)
    assertTrue(AppSettings().fontSize == ListFontSize.Medium)
  }

  @Test
  fun configuredFontSizeAppliesToTextStyle() {
    val textColors = TextColors(textColor = 0xFFFFFFFF.toInt(), useDarkBars = false)

    assertEquals(16f, entryTextStyle(textColors, fontSize = ListFontSize.Small).fontSize.value)
    assertEquals(28f, entryTextStyle(textColors, fontSize = ListFontSize.Large).fontSize.value)
  }

  @Test
  fun allEntriesShareConfiguredFontSize() {
    // The list composable computes the style once and applies it to every row, so each
    // ListFontSize must map to a distinct size across calls.
    val sizes = ListFontSize.entries.map { fontSizeSpFor(it) }

    assertEquals(listOf(16, 22, 28), sizes)
    assertEquals(sizes.toSet().size, sizes.size)
  }
}
