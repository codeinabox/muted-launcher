package io.github.codeinabox.mutedlauncher.apps

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuickActionTest {
  private fun assertTriggerDistance(expectedDp: Float, widthDp: Float) {
    assertEquals(expectedDp, triggerDistance(widthDp.dp).value, 0.001f)
  }

  @Test
  fun normalWidthUsesTwentyPercent() {
    assertTriggerDistance(expectedDp = 100f, widthDp = 500f)
  }

  @Test
  fun smallWidthUsesMinimumClamp() {
    assertTriggerDistance(expectedDp = 72f, widthDp = 300f)
  }

  @Test
  fun tabletWidthUsesMaximumClamp() {
    assertTriggerDistance(expectedDp = 160f, widthDp = 1000f)
  }

  @Test
  fun leftMotionResolvesToCamera() {
    assertEquals(QuickAction.Camera, resolveSwipe(displacementPx = -150f, thresholdPx = 100f))
  }

  @Test
  fun rightMotionResolvesToContacts() {
    assertEquals(QuickAction.Contacts, resolveSwipe(displacementPx = 150f, thresholdPx = 100f))
  }

  @Test
  fun belowThresholdResolvesToNull() {
    assertNull(resolveSwipe(displacementPx = 99f, thresholdPx = 100f))
    assertNull(resolveSwipe(displacementPx = -99f, thresholdPx = 100f))
  }

  @Test
  fun exactThresholdResolvesToAction() {
    assertEquals(QuickAction.Camera, resolveSwipe(displacementPx = -100f, thresholdPx = 100f))
    assertEquals(QuickAction.Contacts, resolveSwipe(displacementPx = 100f, thresholdPx = 100f))
  }
}
