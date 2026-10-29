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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.codeinabox.mutedlauncher.apps.AppEntry
import io.github.codeinabox.mutedlauncher.theme.TextColors

private const val OUTLINE_WIDTH_DP = 1.5f
private const val FONT_SIZE_SP = 22

/** Text shown for an entry: the bare label, or "Label (disambiguator)" when one is present. */
internal fun displayText(app: AppEntry): String =
    app.disambiguator?.let { "${app.label} ($it)" } ?: app.label

/** Fill style for app-list entries. Exposed for testing. */
internal fun entryTextStyle(textColors: TextColors): TextStyle =
    TextStyle(color = Color(textColors.textColor), fontSize = FONT_SIZE_SP.sp)

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

@Composable
fun AppList(
    apps: List<AppEntry>,
    textColors: TextColors,
    onLaunch: (AppEntry) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val style = entryTextStyle(textColors)
  val outlineColor = if (textColors.useDarkBars) Color.White else Color.Black

  LazyColumn(modifier = modifier, contentPadding = contentPadding) {
    items(apps, key = { it.componentName.flattenToString() }) { app ->
      Box(
          Modifier.fillMaxWidth()
              .clickable(interactionSource = null, indication = null) { onLaunch(app) }
              .padding(horizontal = 24.dp, vertical = 10.dp),
      ) {
        OutlinedText(
            text = displayText(app),
            style = style,
            outlineColor = outlineColor,
            maxLines = 1,
        )
      }
    }
  }
}
