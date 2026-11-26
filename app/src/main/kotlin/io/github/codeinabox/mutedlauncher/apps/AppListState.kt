package io.github.codeinabox.mutedlauncher.apps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Stable
class AppListState {
  var apps by mutableStateOf<List<AppEntry>>(emptyList())
    internal set

  /** True once the first load has completed, regardless of how many apps were found. */
  var hasLoaded by mutableStateOf(false)
    internal set

  internal var version by mutableIntStateOf(0)

  fun refresh() {
    version++
  }
}

/** Applies a completed load to [state]: replaces the apps and marks the first load done. */
internal fun applyLoad(state: AppListState, apps: List<AppEntry>) {
  state.apps = apps
  state.hasLoaded = true
}

@Composable
fun rememberAppList(repository: AppRepository): AppListState {
  val state = remember(repository) { AppListState() }

  LaunchedEffect(repository, state.version) {
    applyLoad(state, withContext(Dispatchers.Default) { repository.loadApps() })
  }

  DisposableEffect(repository, state) {
    val unregister = repository.registerChangeListener { state.refresh() }
    onDispose(unregister)
  }

  return state
}
