package io.github.codeinabox.mutedlauncher.apps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.LauncherApps
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle

class AppRepository(context: Context) {
  private val launcherApps = context.getSystemService(LauncherApps::class.java)

  fun loadApps(): List<AppEntry> =
      sortApps(
          disambiguateCollisions(
              launcherApps.getActivityList(null, Process.myUserHandle()).map {
                AppEntry(label = it.label.toString(), componentName = it.componentName)
              },
          ),
      )

  /** Returns false when the app can no longer be launched. */
  fun launch(app: AppEntry): Boolean =
      try {
        launcherApps.startMainActivity(app.componentName, Process.myUserHandle(), null, null)
        true
      } catch (_: ActivityNotFoundException) {
        false
      } catch (_: SecurityException) {
        false
      }

  fun registerChangeListener(onChange: () -> Unit): () -> Unit {
    val callback =
        object : LauncherApps.Callback() {
          override fun onPackageAdded(packageName: String, user: UserHandle) = onChange()

          override fun onPackageChanged(packageName: String, user: UserHandle) = onChange()

          override fun onPackageRemoved(packageName: String, user: UserHandle) = onChange()

          override fun onPackagesAvailable(
              packageNames: Array<out String>,
              user: UserHandle,
              replacing: Boolean,
          ) = onChange()

          override fun onPackagesUnavailable(
              packageNames: Array<out String>,
              user: UserHandle,
              replacing: Boolean,
          ) = onChange()
        }
    launcherApps.registerCallback(callback, Handler(Looper.getMainLooper()))
    return { launcherApps.unregisterCallback(callback) }
  }
}
