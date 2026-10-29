package io.github.codeinabox.mutedlauncher.apps

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRepositoryTest {
  private val context = InstrumentationRegistry.getInstrumentation().targetContext

  @Test
  fun appWithLauncherEntryAppearsExactlyOnce() {
    val settings =
        AppRepository(context).loadApps().filter {
          it.componentName.packageName == "com.android.settings"
        }
    assertEquals(1, settings.size)
    assertTrue(settings.single().label.isNotBlank())
  }

  @Test
  fun appWithoutLauncherEntryDoesNotAppear() {
    // This app declares only a HOME entry, so it has no launcher entry of its own.
    val own =
        AppRepository(context).loadApps().filter {
          it.componentName.packageName == context.packageName
        }
    assertTrue(own.isEmpty())
  }

  @Test
  fun launchingAnAppThatNoLongerExistsReturnsFalse() {
    val missing =
        AppEntry("Gone", android.content.ComponentName("no.such.package", "no.such.package.Main"))
    assertFalse(AppRepository(context).launch(missing))
  }
}
