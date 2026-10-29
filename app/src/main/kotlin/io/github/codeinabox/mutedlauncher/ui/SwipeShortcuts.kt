package io.github.codeinabox.mutedlauncher.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.codeinabox.mutedlauncher.apps.QuickAction
import io.github.codeinabox.mutedlauncher.apps.resolveSwipe
import io.github.codeinabox.mutedlauncher.apps.triggerDistance
import kotlin.math.abs

/**
 * Covers [content] with a full-size horizontal swipe surface that reports a resolved quick action
 * on release. The surface leaves taps, vertical scrolling, and the system gesture edges untouched.
 */
@Composable
fun SwipeShortcutHandler(
    onSwipe: (QuickAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
  val currentOnSwipe by rememberUpdatedState(onSwipe)
  BoxWithConstraints(modifier = modifier) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val thresholdPx = with(density) { triggerDistance(maxWidth).toPx() }
    val systemGestureInsets = WindowInsets.systemGestures
    Box(
        modifier =
            Modifier.fillMaxSize().pointerInput(thresholdPx, systemGestureInsets) {
              detectSwipeShortcuts(
                  thresholdPx = thresholdPx,
                  systemGestureInsets = systemGestureInsets,
                  layoutDirection = layoutDirection,
                  onSwipe = { action -> currentOnSwipe(action) },
              )
            },
        content = content,
    )
  }
}

private suspend fun PointerInputScope.detectSwipeShortcuts(
    thresholdPx: Float,
    systemGestureInsets: WindowInsets,
    layoutDirection: LayoutDirection,
    onSwipe: (QuickAction) -> Unit,
) {
  val pointerDensity: Density = this
  awaitEachGesture {
    val down = awaitFirstDown(requireUnconsumed = false)
    if (
        startsInSystemGestureArea(
            x = down.position.x,
            width = size.width,
            insets = systemGestureInsets,
            density = pointerDensity,
            layoutDirection = layoutDirection,
        )
    ) {
      return@awaitEachGesture
    }
    val displacement = awaitHorizontalDisplacement(down) ?: return@awaitEachGesture
    resolveSwipe(displacement, thresholdPx)?.let(onSwipe)
  }
}

/** True when [x] falls inside the physical left or right system gesture inset. */
private fun startsInSystemGestureArea(
    x: Float,
    width: Int,
    insets: WindowInsets,
    density: Density,
    layoutDirection: LayoutDirection,
): Boolean {
  val left = insets.getLeft(density, layoutDirection)
  val right = insets.getRight(density, layoutDirection)
  return x < left || x > width - right
}

/**
 * Waits for the first touch slop, then returns the horizontal displacement if the horizontal axis
 * won. Returns null when the gesture was cancelled or the vertical axis won.
 */
private suspend fun AwaitPointerEventScope.awaitHorizontalDisplacement(
    down: PointerInputChange,
): Float? {
  val touchSlop = viewConfiguration.touchSlop
  var horizontal = 0f
  var vertical = 0f
  var result: Float? = null
  var decided = false

  while (!decided) {
    val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id }
    if (change == null || change.isConsumed) {
      decided = true
    } else {
      val delta = change.positionChange()
      horizontal += delta.x
      vertical += delta.y
      val pastSlop = abs(horizontal) > touchSlop || abs(vertical) > touchSlop
      val horizontalWins = pastSlop && abs(horizontal) > abs(vertical)
      val verticalWins = pastSlop && !horizontalWins
      if (horizontalWins) {
        decided = true
        result = consumeAndAwaitRelease(change, horizontal)
      } else if (verticalWins) {
        decided = true
      }
    }
  }
  return result
}

/** Consumes the winning change and keeps consuming drag changes until the pointer is released. */
private suspend fun AwaitPointerEventScope.consumeAndAwaitRelease(
    change: PointerInputChange,
    displacement: Float,
): Float? {
  change.consume()
  return if (change.changedToUpIgnoreConsumed()) displacement
  else awaitRemainingHorizontal(change.id, displacement)
}

/** Consumes and accumulates horizontal drag changes until the pointer is released. */
private suspend fun AwaitPointerEventScope.awaitRemainingHorizontal(
    pointerId: PointerId,
    initialDisplacement: Float,
): Float? {
  var displacement = initialDisplacement
  while (true) {
    val change = awaitPointerEvent().changes.firstOrNull { it.id == pointerId }
    if (change == null || change.isConsumed) return null
    if (change.changedToUpIgnoreConsumed()) return displacement
    displacement += change.positionChange().x
    change.consume()
  }
}
