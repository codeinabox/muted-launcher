package io.github.codeinabox.mutedlauncher.apps

/** Splits a package segment into readable words, e.g. "GoogleCamera" -> "Google Camera". */
internal fun humanizePackageSegment(segment: String): String {
  val words = segment.replace(Regex("(?<=[a-z0-9])(?=\\p{Upper})"), " ")
  return words.replaceFirstChar { it.titlecase() }
}

/**
 * Derives a disambiguator for an app from its package name: the humanised last segment, or that
 * segment prefixed with the nearest non-generic earlier segment when the last one alone matches the
 * label case-insensitively (so the displayed text still differs from the bare label).
 */
internal fun packageDisambiguator(packageName: String, label: String): String {
  val segments = packageName.split('.')
  val last = segments.last()
  val humanized = humanizePackageSegment(last)
  if (humanized.equals(label, ignoreCase = true)) {
    val previous =
        segments
            .dropLast(1)
            .reversed()
            .firstOrNull { it !in GENERIC_PACKAGE_SEGMENTS }
            ?.let(::humanizePackageSegment)
    if (previous != null) return "$previous $humanized"
  }
  return humanized
}

private val GENERIC_PACKAGE_SEGMENTS = setOf("app", "apps", "android", "com", "net", "org")

/**
 * Attaches a package-derived disambiguator to every entry whose label is shared with at least one
 * other entry. Entries with unique labels are returned unchanged with a null disambiguator.
 *
 * [packageNameOf] exists as a seam for JVM unit tests, where `ComponentName` methods are unmocked.
 */
internal fun disambiguateCollisions(
    apps: List<AppEntry>,
    packageNameOf: (AppEntry) -> String = { it.componentName.packageName },
): List<AppEntry> {
  val counts = apps.groupingBy { it.label }.eachCount()
  return apps.map { app ->
    if (counts.getValue(app.label) > 1) {
      app.copy(disambiguator = packageDisambiguator(packageNameOf(app), app.label))
    } else {
      app
    }
  }
}
