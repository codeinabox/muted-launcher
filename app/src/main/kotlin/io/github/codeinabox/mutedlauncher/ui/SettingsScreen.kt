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
        AlignmentSettingRow(alignment = alignment, onAlignmentChange = onAlignmentChange)
        Spacer(modifier = Modifier.height(24.dp))
        OutlineSettingRow(outlineEnabled = outlineEnabled, onOutlineChange = onOutlineChange)
        Spacer(modifier = Modifier.height(24.dp))
        FontSizeSettingRow(fontSize = fontSize, onFontSizeChange = onFontSizeChange)
      }
    }
  }
}

/** Title label used by the settings rows. */
@Composable
private fun SettingLabel(text: String, modifier: Modifier = Modifier) {
  Text(
      text = text,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = modifier,
  )
}

/** A one-choice segmented row over an enum's entries, shared by the enum-backed settings. */
@Composable
private fun <T : Enum<T>> SegmentedChoiceRow(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
  SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxSize()) {
    options.forEachIndexed { index, option ->
      SegmentedButton(
          selected = selected == option,
          onClick = { onSelect(option) },
          shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
      ) {
        Text(text = option.name)
      }
    }
  }
}

/** The app-list alignment setting: a label plus one segmented choice row. */
@Composable
private fun AlignmentSettingRow(
    alignment: ListAlignment,
    onAlignmentChange: (ListAlignment) -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    SettingLabel(text = "App list alignment")
    Spacer(modifier = Modifier.height(8.dp))
    SegmentedChoiceRow(
        options = alignmentOptions,
        selected = alignment,
        onSelect = onAlignmentChange,
    )
  }
}

/** The font-size setting: a label plus one segmented choice row. */
@Composable
private fun FontSizeSettingRow(
    fontSize: ListFontSize,
    onFontSizeChange: (ListFontSize) -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    SettingLabel(text = "Font size")
    Spacer(modifier = Modifier.height(8.dp))
    SegmentedChoiceRow(
        options = fontSizeOptions,
        selected = fontSize,
        onSelect = onFontSizeChange,
    )
  }
}

/** The text-outline setting: a labeled row with the toggle at the end. */
@Composable
private fun OutlineSettingRow(
    outlineEnabled: Boolean,
    onOutlineChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(modifier = modifier.fillMaxWidth()) {
    Column(modifier = Modifier.weight(1f)) {
      SettingLabel(text = "Text outline")
      Text(
          text = "Draw app list text with an outline",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(checked = outlineEnabled, onCheckedChange = onOutlineChange)
  }
}
