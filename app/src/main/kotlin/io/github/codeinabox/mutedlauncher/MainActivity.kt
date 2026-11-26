package io.github.codeinabox.mutedlauncher

import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.codeinabox.mutedlauncher.apps.AppRepository
import io.github.codeinabox.mutedlauncher.apps.QuickActionLauncher
import io.github.codeinabox.mutedlauncher.apps.rememberAppList
import io.github.codeinabox.mutedlauncher.settings.AppSettings
import io.github.codeinabox.mutedlauncher.settings.DataStoreSettingsRepository
import io.github.codeinabox.mutedlauncher.settings.ListAlignment
import io.github.codeinabox.mutedlauncher.settings.ListFontSize
import io.github.codeinabox.mutedlauncher.settings.settingsDataStore
import io.github.codeinabox.mutedlauncher.theme.TextColors
import io.github.codeinabox.mutedlauncher.theme.rememberWallpaperTextColors
import io.github.codeinabox.mutedlauncher.ui.AppList
import io.github.codeinabox.mutedlauncher.ui.SettingsScreen
import io.github.codeinabox.mutedlauncher.ui.SwipeShortcutHandler
import kotlinx.coroutines.launch

/** Top-level screens of the launcher; the app list is the home state. */
sealed interface Screen {
  data object AppList : Screen

  data object Settings : Screen
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
    )
    window.isNavigationBarContrastEnforced = false
    setContent { LauncherRoot(window) }
  }
}

/** Launcher content: the app list by default, the settings screen when opened. */
@Composable
private fun LauncherRoot(window: Window) {
  val textColors by rememberWallpaperTextColors()
  val view = LocalView.current
  val context = LocalContext.current
  SideEffect {
    val controller = WindowCompat.getInsetsController(window, view)
    controller.isAppearanceLightStatusBars = textColors.useDarkBars
    controller.isAppearanceLightNavigationBars = textColors.useDarkBars
  }

  val settingsRepository = remember { DataStoreSettingsRepository(context.settingsDataStore) }
  val settings by settingsRepository.settings.collectAsState(initial = AppSettings())

  var screen by remember { mutableStateOf<Screen>(Screen.AppList) }
  BackHandler(enabled = screen != Screen.AppList) { screen = Screen.AppList }

  when (screen) {
    Screen.AppList ->
        AppListScreen(
            alignment = settings.alignment,
            outlineEnabled = settings.outlineEnabled,
            fontSize = settings.fontSize,
            textColors = textColors,
            onOpenSettings = { screen = Screen.Settings },
        )

    Screen.Settings ->
        SettingsScreenRoute(
            alignment = settings.alignment,
            onAlignmentChange = { alignment -> settingsRepository.setAlignment(alignment) },
            outlineEnabled = settings.outlineEnabled,
            onOutlineChange = { enabled -> settingsRepository.setOutlineEnabled(enabled) },
            fontSize = settings.fontSize,
            onFontSizeChange = { size -> settingsRepository.setFontSize(size) },
        )
  }
}

/** The home screen: swipe shortcuts wrapping the app list. */
@Composable
private fun AppListScreen(
    alignment: ListAlignment,
    outlineEnabled: Boolean,
    fontSize: ListFontSize,
    textColors: TextColors,
    onOpenSettings: () -> Unit,
) {
  val context = LocalContext.current
  val repository = remember { AppRepository(context) }
  val quickActionLauncher = remember { QuickActionLauncher(context) }
  val appList = rememberAppList(repository)
  SwipeShortcutHandler(
      onSwipe = { action -> quickActionLauncher.launch(action) },
      modifier = Modifier.fillMaxSize(),
  ) {
    AppList(
        apps = appList.apps,
        listLoaded = appList.hasLoaded,
        textColors = textColors,
        onLaunch = { app -> if (!repository.launch(app)) appList.refresh() },
        onOpenSettings = onOpenSettings,
        alignment = alignment,
        outlineEnabled = outlineEnabled,
        fontSize = fontSize,
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.asPaddingValues(),
    )
  }
}

/** The settings screen; writes go through the repository. */
@Composable
private fun SettingsScreenRoute(
    alignment: ListAlignment,
    onAlignmentChange: suspend (ListAlignment) -> Unit,
    outlineEnabled: Boolean,
    onOutlineChange: suspend (Boolean) -> Unit,
    fontSize: ListFontSize,
    onFontSizeChange: suspend (ListFontSize) -> Unit,
) {
  val scope = rememberCoroutineScope()
  SettingsScreen(
      alignment = alignment,
      onAlignmentChange = { alignment -> scope.launch { onAlignmentChange(alignment) } },
      outlineEnabled = outlineEnabled,
      onOutlineChange = { enabled -> scope.launch { onOutlineChange(enabled) } },
      fontSize = fontSize,
      onFontSizeChange = { size -> scope.launch { onFontSizeChange(size) } },
      modifier = Modifier.fillMaxSize(),
      contentPadding = WindowInsets.systemBars.asPaddingValues(),
  )
}
