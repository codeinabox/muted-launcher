package io.github.codeinabox.mutedlauncher.apps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.provider.MediaStore

/** Launches the device's default handlers for the external quick actions. */
class QuickActionLauncher(context: Context) {
  private val appContext = context.applicationContext

  /** Returns false when no app can handle the action. */
  fun launch(action: QuickAction): Boolean =
      try {
        appContext.startActivity(intentFor(action))
        true
      } catch (_: ActivityNotFoundException) {
        false
      }

  private fun intentFor(action: QuickAction): Intent =
      when (action) {
        QuickAction.Camera -> Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        QuickAction.Contacts -> Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
      }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
