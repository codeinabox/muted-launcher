package io.github.codeinabox.mutedlauncher.apps

import android.content.ComponentName

data class AppEntry(
    val label: String,
    val componentName: ComponentName,
    val disambiguator: String? = null,
)
