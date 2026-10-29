package io.github.codeinabox.mutedlauncher.apps

import android.content.ComponentName
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppDisambiguationTest {
  @Test
  fun camelCaseSegmentSplitsIntoWords() {
    assertEquals("Google Camera", humanizePackageSegment("GoogleCamera"))
  }

  @Test
  fun lowercaseSegmentIsCapitalised() {
    assertEquals("Cameras2", humanizePackageSegment("cameras2"))
  }

  @Test
  fun lastSegmentIsHumanised() {
    assertEquals("Google Camera", packageDisambiguator("com.google.android.GoogleCamera", "Camera"))
  }

  @Test
  fun labelMatchingSegmentFallsBackToVendorSegment() {
    assertEquals(
        "Sec Weather",
        packageDisambiguator("com.sec.android.app.weather", "Weather"),
    )
  }

  @Test
  fun labelMatchingASingleGenericSegmentStaysHumanised() {
    assertEquals("Weather", packageDisambiguator("weather", "Weather"))
  }

  @Test
  fun labelMatchingLastSegmentWithNoVendorSegmentStaysHumanised() {
    assertEquals("Weather", packageDisambiguator("com.android.weather", "Weather"))
  }

  private fun entry(label: String, packageName: String) =
      AppEntry(label, ComponentName(packageName, "$packageName.Main")) to packageName

  private fun collide(apps: List<Pair<AppEntry, String>>): List<AppEntry> {
    val entries = apps.map { it.first }
    val packages = apps.map { it.second }
    return disambiguateCollisions(entries) { e -> packages[entries.indexOfFirst { it === e }] }
  }

  @Test
  fun uniqueLabelsGetNoDisambiguator() {
    val apps = listOf(entry("Clock", "com.example.clock"), entry("Camera", "com.example.cam"))
    collide(apps).forEach { assertNull(it.disambiguator) }
  }

  @Test
  fun everyMemberOfADuplicateGroupGetsADisambiguator() {
    val apps =
        listOf(
            entry("Camera", "com.android.cameras2"),
            entry("Clock", "com.example.clock"),
            entry("Camera", "com.google.android.GoogleCamera"),
        )
    val result = collide(apps)
    assertEquals("Cameras2", result[0].disambiguator)
    assertNull(result[1].disambiguator)
    assertEquals("Google Camera", result[2].disambiguator)
  }

  @Test
  fun disambiguatedEntriesKeepTheirCollatedLabelPosition() {
    val apps =
        listOf(
            entry("Weather", "com.example.forecast"),
            entry("Camera", "com.android.cameras2"),
            entry("Weather", "com.sec.android.app.weather"),
            entry("Clock", "com.example.clock"),
            entry("Camera", "com.google.android.GoogleCamera"),
        )
    val sorted = sortApps(collide(apps), Locale.ENGLISH)
    assertEquals(listOf("Camera", "Camera", "Clock", "Weather", "Weather"), sorted.map { it.label })
    assertEquals("Cameras2", sorted[0].disambiguator)
    assertEquals("Google Camera", sorted[1].disambiguator)
    assertNull(sorted[2].disambiguator)
    assertEquals("Forecast", sorted[3].disambiguator)
    assertEquals("Sec Weather", sorted[4].disambiguator)
  }
}
