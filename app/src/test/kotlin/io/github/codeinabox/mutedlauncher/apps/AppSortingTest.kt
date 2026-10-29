package io.github.codeinabox.mutedlauncher.apps

import android.content.ComponentName
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class AppSortingTest {
  private fun labels(vararg names: String, locale: Locale) =
      sortApps(names.mapIndexed { i, n -> AppEntry(n, ComponentName("pkg.a$i", "Main")) }, locale)
          .map { it.label }

  @Test
  fun ignoresCase() {
    assertEquals(
        listOf("Calendar", "camera", "Clock"),
        labels("camera", "Calendar", "Clock", locale = Locale.ENGLISH),
    )
  }

  @Test
  fun accentedNamesFollowLocaleCollationNotCharacterCode() {
    assertEquals(
        listOf("Éclair", "Evernote"),
        labels("Evernote", "Éclair", locale = Locale.FRENCH),
    )
  }

  @Test
  fun equalLabelsKeepInputOrder() {
    val first = AppEntry("Notes", ComponentName("pkg.b", "Main"))
    val second = AppEntry("Notes", ComponentName("pkg.a", "Main"))
    val sorted = sortApps(listOf(first, second), Locale.ENGLISH)
    assertSame(first, sorted[0])
    assertSame(second, sorted[1])
  }
}
