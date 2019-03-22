package org.simple.clinic.router.screen

import android.os.Parcelable
import android.view.View
import androidx.annotation.LayoutRes
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import flow.Direction

/**
 *
 *  Screens can receive payloads inside their associated keys by calling [ScreenRouter.key].
 *
 * Note: use AutoValue or otherwise ensure equals() is overridden.
 * Screen routing is skipped if an outgoing key and an incoming key are equal.
 */
interface FullScreenKey : Parcelable {

  val analyticsName: String

  @LayoutRes
  fun layoutRes(): Int

  fun animate(
      outgoingView: View,
      incomingView: View,
      direction: Direction,
      outgoingKey: FullScreenKey?,
      onCompleteListener: () -> Unit
  ) {
    val scaleChange = 0.05f
    val duration = SCREEN_CHANGE_ANIMATION_DURATION
    val interpolator = FastOutSlowInInterpolator()

    if (direction == Direction.FORWARD || direction == Direction.REPLACE) {
      outgoingView.animate()
          .scaleX(1f - scaleChange)
          .scaleY(1f - scaleChange)
          .alpha(0f)
          .setDuration(duration)
          .setInterpolator(interpolator)
          .start()

    } else {
      outgoingView.animate()
          .scaleX(1f + scaleChange)
          .scaleY(1f + scaleChange)
          .alpha(0f)
          .setDuration(duration)
          .setInterpolator(interpolator)
          .start()
    }

    if (direction == Direction.FORWARD || direction == Direction.REPLACE) {
      incomingView.scaleX = 1f + scaleChange
      incomingView.scaleY = 1f + scaleChange
      incomingView.alpha = 0f
      incomingView.animate()
          .scaleX(1f)
          .scaleY(1f)
          .alpha(1f)
          .setDuration(duration)
          .setInterpolator(interpolator)
          .withEndAction {
            onCompleteListener.invoke()
          }
          .start()

    } else {
      incomingView.scaleX = 1f - scaleChange
      incomingView.scaleY = 1f - scaleChange
      incomingView.alpha = 0f
      incomingView.animate()
          .scaleX(1f)
          .scaleY(1f)
          .alpha(1f)
          .setDuration(duration)
          .setInterpolator(interpolator)
          .withEndAction {
            onCompleteListener.invoke()
          }
          .start()
    }
  }
}
