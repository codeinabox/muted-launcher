package io.github.codeinabox.mutedlauncher.apps

import java.text.Collator
import java.util.Locale

fun sortApps(apps: List<AppEntry>, locale: Locale = Locale.getDefault()): List<AppEntry> {
  val collator = Collator.getInstance(locale).apply { strength = Collator.SECONDARY }
  return apps
      .map { it to collator.getCollationKey(it.label) }
      .sortedBy { it.second }
      .map { it.first }
}
