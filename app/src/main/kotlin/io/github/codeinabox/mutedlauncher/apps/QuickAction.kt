package io.github.codeinabox.mutedlauncher.apps

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val TRIGGER_FRACTION = 0.20f
private val MIN_TRIGGER_DISTANCE = 72.dp
private val MAX_TRIGGER_DISTANCE = 160.dp

/** The external quick actions reachable through a horizontal swipe. */
enum class QuickAction {
  Camera,
  Contacts,
}

/** Horizontal distance a swipe must cover before it launches a quick action. */
fun triggerDistance(widthDp: Dp): Dp =
    (widthDp * TRIGGER_FRACTION).coerceIn(MIN_TRIGGER_DISTANCE, MAX_TRIGGER_DISTANCE)

/**
 * Resolves a completed swipe from its horizontal displacement.
 *
 * Negative displacement is a right-to-left motion (camera), positive displacement is a
 * left-to-right motion (contacts). Displacements within the threshold resolve to null.
 */
fun resolveSwipe(displacementPx: Float, thresholdPx: Float): QuickAction? =
    when {
      displacementPx <= -thresholdPx -> QuickAction.Camera
      displacementPx >= thresholdPx -> QuickAction.Contacts
      else -> null
    }
