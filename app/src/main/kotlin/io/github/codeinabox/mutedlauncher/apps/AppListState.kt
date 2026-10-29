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

  internal var version by mutableIntStateOf(0)

  fun refresh() {
    version++
  }
}

@Composable
fun rememberAppList(repository: AppRepository): AppListState {
  val state = remember(repository) { AppListState() }

  LaunchedEffect(repository, state.version) {
    state.apps = withContext(Dispatchers.Default) { repository.loadApps() }
  }

  DisposableEffect(repository, state) {
    val unregister = repository.registerChangeListener { state.refresh() }
    onDispose(unregister)
  }

  return state
}
