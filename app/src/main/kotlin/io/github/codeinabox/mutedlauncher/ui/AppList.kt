package io.github.codeinabox.mutedlauncher.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.codeinabox.mutedlauncher.apps.AppEntry
import io.github.codeinabox.mutedlauncher.settings.ListAlignment
import io.github.codeinabox.mutedlauncher.settings.ListFontSize
import io.github.codeinabox.mutedlauncher.theme.TextColors

private const val OUTLINE_WIDTH_DP = 1.5f
private const val SMALL_FONT_SIZE_SP = 16
private const val MEDIUM_FONT_SIZE_SP = 22
private const val LARGE_FONT_SIZE_SP = 28

/** Sp size for entry text, a pure function of [ListFontSize]. Exposed for testing. */
internal fun fontSizeSpFor(size: ListFontSize): Int =
    when (size) {
      ListFontSize.Small -> SMALL_FONT_SIZE_SP
      ListFontSize.Medium -> MEDIUM_FONT_SIZE_SP
      ListFontSize.Large -> LARGE_FONT_SIZE_SP
    }

/** Text shown for an entry: the bare label, or "Label (disambiguator)" when one is present. */
internal fun displayText(app: AppEntry): String =
    app.disambiguator?.let { "${app.label} ($it)" } ?: app.label

/** Key of the settings entry rendered as the final list item. */
internal const val SETTINGS_ROW_KEY = "settings"

/** Label of the settings entry, styled like any app row. */
internal const val SETTINGS_ROW_LABEL = "Launcher Settings"

/** One tappable row of the app list: an app entry, or the settings entry when [app] is null. */
internal data class AppListRow(val key: Any, val app: AppEntry?) {
  val text: String
    get() = app?.let(::displayText) ?: SETTINGS_ROW_LABEL
}

/**
 * All list rows in render order: apps first, settings entry last. The settings entry appears only
 * once the list has finished loading ([listLoaded]), so it is not shown alone on first launch.
 * [appKey] derives the stable LazyColumn key for an app row; it is injectable so JVM tests avoid
 * framework calls. Exposed for testing.
 */
internal fun appListRows(
    apps: List<AppEntry>,
    listLoaded: Boolean,
    appKey: (AppEntry) -> Any = { it.componentName },
): List<AppListRow> =
    apps.map { AppListRow(key = appKey(it), app = it) } +
        if (listLoaded) listOf(AppListRow(key = SETTINGS_ROW_KEY, app = null)) else emptyList()

/** True when a row is the settings entry. Exposed for testing. */
internal fun isSettingsRow(row: AppListRow): Boolean = row.app == null

/** Text alignment for entry text, a pure function of [ListAlignment]. Exposed for testing. */
internal fun textAlignFor(alignment: ListAlignment): TextAlign =
    when (alignment) {
      ListAlignment.Left -> TextAlign.Left
      ListAlignment.Center -> TextAlign.Center
      ListAlignment.Right -> TextAlign.Right
    }

/**
 * Row-box content alignment for entry text, a pure function of [ListAlignment]. Exposed for
 * testing.
 */
internal fun boxAlignmentFor(alignment: ListAlignment): Alignment =
    when (alignment) {
      ListAlignment.Left -> Alignment.CenterStart
      ListAlignment.Center -> Alignment.Center
      ListAlignment.Right -> Alignment.CenterEnd
    }

/** Fill style for app-list entries. Exposed for testing. */
internal fun entryTextStyle(
    textColors: TextColors,
    alignment: ListAlignment = ListAlignment.Left,
    fontSize: ListFontSize = ListFontSize.Medium,
): TextStyle =
    TextStyle(
        color = Color(textColors.textColor),
        fontSize = fontSizeSpFor(fontSize).sp,
        textAlign = textAlignFor(alignment),
    )

/** Outline style derived from the fill style. Exposed for testing. */
internal fun outlineTextStyle(
    fillStyle: TextStyle,
    outlineColor: Color,
    density: Density,
): TextStyle =
    fillStyle.copy(
        color = outlineColor,
        shadow = null,
        drawStyle = Stroke(width = with(density) { OUTLINE_WIDTH_DP.dp.toPx() }),
    )

@Composable
private fun OutlinedText(
    text: String,
    style: TextStyle,
    outlineColor: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
) {
  val density = LocalDensity.current
  val outlineStyle =
      remember(style, outlineColor, density) { outlineTextStyle(style, outlineColor, density) }

  Box(modifier) {
    BasicText(text, style = outlineStyle, maxLines = maxLines)
    BasicText(text, style = style, maxLines = maxLines)
  }
}

/**
 * Entry text with the outline applied when [outlineEnabled] is on, plain fill otherwise; one call
 * site keeps every row — app entries and the settings entry — on the same treatment.
 */
@Composable
private fun EntryText(
    text: String,
    style: TextStyle,
    outlineColor: Color,
    outlineEnabled: Boolean,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
) {
  if (outlineEnabled) {
    OutlinedText(text, style, outlineColor, modifier, maxLines)
  } else {
    Box(modifier) { BasicText(text, style = style, maxLines = maxLines) }
  }
}

@Composable
fun AppList(
    apps: List<AppEntry>,
    listLoaded: Boolean,
    textColors: TextColors,
    onLaunch: (AppEntry) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: ListAlignment = ListAlignment.Left,
    outlineEnabled: Boolean = true,
    fontSize: ListFontSize = ListFontSize.Medium,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val style = entryTextStyle(textColors, alignment, fontSize)
  val outlineColor = if (textColors.useDarkBars) Color.White else Color.Black

  LazyColumn(modifier = modifier, contentPadding = contentPadding) {
    items(appListRows(apps, listLoaded), key = { it.key }) { row ->
      Box(
          Modifier.fillMaxWidth()
              .clickable(interactionSource = null, indication = null) {
                if (isSettingsRow(row)) onOpenSettings() else row.app?.let(onLaunch)
              }
              .padding(horizontal = 24.dp, vertical = 10.dp),
          contentAlignment = boxAlignmentFor(alignment),
      ) {
        EntryText(
            text = row.text,
            style = style,
            outlineColor = outlineColor,
            outlineEnabled = outlineEnabled,
            maxLines = 1,
        )
      }
    }
  }
}
