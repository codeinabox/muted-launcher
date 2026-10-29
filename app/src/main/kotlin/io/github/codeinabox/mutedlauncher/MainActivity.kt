package io.github.codeinabox.mutedlauncher

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.codeinabox.mutedlauncher.apps.AppRepository
import io.github.codeinabox.mutedlauncher.apps.QuickActionLauncher
import io.github.codeinabox.mutedlauncher.apps.rememberAppList
import io.github.codeinabox.mutedlauncher.theme.rememberWallpaperTextColors
import io.github.codeinabox.mutedlauncher.ui.AppList
import io.github.codeinabox.mutedlauncher.ui.SwipeShortcutHandler

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
    )
    window.isNavigationBarContrastEnforced = false
    setContent {
      BackHandler {}
      val textColors by rememberWallpaperTextColors()
      val view = LocalView.current
      SideEffect {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = textColors.useDarkBars
        controller.isAppearanceLightNavigationBars = textColors.useDarkBars
      }

      val repository = remember { AppRepository(applicationContext) }
      val quickActionLauncher = remember { QuickActionLauncher(applicationContext) }
      val appList = rememberAppList(repository)
      SwipeShortcutHandler(
          onSwipe = { action -> quickActionLauncher.launch(action) },
          modifier = Modifier.fillMaxSize(),
      ) {
        AppList(
            apps = appList.apps,
            textColors = textColors,
            onLaunch = { app -> if (!repository.launch(app)) appList.refresh() },
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
        )
      }
    }
  }
}
