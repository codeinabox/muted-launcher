package io.github.codeinabox.mutedlauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.codeinabox.mutedlauncher.settings.ListAlignment
import io.github.codeinabox.mutedlauncher.settings.ListFontSize

/** Settings rows offered today; grows as settings are added. */
private val alignmentOptions = ListAlignment.entries
private val fontSizeOptions = ListFontSize.entries

/**
 * The launcher's settings screen, built with Material 3 and the wallpaper-derived dynamic color
 * scheme. Each row writes through the repository via the change callbacks.
 */
@Composable
fun SettingsScreen(
    alignment: ListAlignment,
    onAlignmentChange: (ListAlignment) -> Unit,
    outlineEnabled: Boolean,
    onOutlineChange: (Boolean) -> Unit,
    fontSize: ListFontSize,
    onFontSizeChange: (ListFontSize) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val colorScheme =
      if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current)
      else dynamicLightColorScheme(LocalContext.current)

  MaterialTheme(colorScheme = colorScheme) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
    ) {
      Column(modifier = Modifier.padding(24.dp)) {
        Text(text = "Launcher Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "App list alignment",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxSize()) {
          alignmentOptions.forEachIndexed { index, option ->
            SegmentedButton(
                selected = alignment == option,
                onClick = { onAlignmentChange(option) },
                shape =
                    SegmentedButtonDefaults.itemShape(index = index, count = alignmentOptions.size),
            ) {
              Text(text = option.name)
            }
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Text outline",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Draw app list text with an outline",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Switch(checked = outlineEnabled, onCheckedChange = onOutlineChange)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Font size",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxSize()) {
          fontSizeOptions.forEachIndexed { index, option ->
            SegmentedButton(
                selected = fontSize == option,
                onClick = { onFontSizeChange(option) },
                shape =
                    SegmentedButtonDefaults.itemShape(index = index, count = fontSizeOptions.size),
            ) {
              Text(text = option.name)
            }
          }
        }
      }
    }
  }
}
