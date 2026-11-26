package io.github.codeinabox.mutedlauncher.apps

import android.content.ComponentName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppListStateTest {
  private fun entry(label: String) = AppEntry(label, ComponentName("pkg", "Main"))

  /** ComponentName.equals is not mocked on the JVM, so compare entries by label. */
  private fun labels(apps: List<AppEntry>) = apps.map { it.label }

  @Test
  fun initialStateIsNotLoadedWithEmptyApps() {
    val state = AppListState()

    assertFalse(state.hasLoaded)
    assertEquals(emptyList<AppEntry>(), state.apps)
  }

  @Test
  fun applyLoadMarksFirstLoadDoneWithApps() {
    val state = AppListState()

    applyLoad(state, listOf(entry("Clock")))

    assertTrue(state.hasLoaded)
    assertEquals(listOf("Clock"), labels(state.apps))
  }

  @Test
  fun applyLoadMarksFirstLoadDoneRegardlessOfResultCount() {
    val state = AppListState()

    applyLoad(state, emptyList())

    assertTrue(state.hasLoaded)
    assertEquals(emptyList<AppEntry>(), state.apps)
  }

  @Test
  fun applyLoadKeepsLoadedFlagTrueOnRefresh() {
    val state = AppListState()
    applyLoad(state, listOf(entry("Clock")))

    applyLoad(state, emptyList())

    assertTrue(state.hasLoaded)
  }
}
