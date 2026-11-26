package io.github.codeinabox.mutedlauncher.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {
  private val alignmentKey = stringPreferencesKey(KEY_ALIGNMENT)
  private val outlineKey = booleanPreferencesKey(KEY_OUTLINE_ENABLED)
  private val fontSizeKey = stringPreferencesKey(KEY_FONT_SIZE)

  @Test
  fun defaultsToLeftAlignmentWhenNothingStored() {
    assertEquals(AppSettings(), appSettingsFrom(preferencesOf()))
  }

  @Test
  fun mapsStoredAlignmentValueToEnum() {
    assertEquals(
        ListAlignment.Center,
        appSettingsFrom(preferencesOf(alignmentKey to "Center")).alignment,
    )
  }

  @Test
  fun fallsBackToDefaultOnUnknownStoredValue() {
    assertEquals(
        ListAlignment.Left,
        appSettingsFrom(preferencesOf(alignmentKey to "Sideways")).alignment,
    )
  }

  @Test
  fun defaultsToOutlinedTextWhenNothingStored() {
    assertTrue(appSettingsFrom(preferencesOf()).outlineEnabled)
  }

  @Test
  fun mapsStoredOutlineValueToBoolean() {
    assertTrue(appSettingsFrom(preferencesOf(outlineKey to true)).outlineEnabled)
    assertFalse(appSettingsFrom(preferencesOf(outlineKey to false)).outlineEnabled)
  }

  @Test
  fun defaultsToMediumFontSizeWhenNothingStored() {
    assertEquals(ListFontSize.Medium, appSettingsFrom(preferencesOf()).fontSize)
  }

  @Test
  fun mapsStoredFontSizeValueToEnum() {
    assertEquals(
        ListFontSize.Large,
        appSettingsFrom(preferencesOf(fontSizeKey to "Large")).fontSize,
    )
  }

  @Test
  fun fallsBackToMediumOnUnknownStoredFontSize() {
    assertEquals(
        ListFontSize.Medium,
        appSettingsFrom(preferencesOf(fontSizeKey to "Huge")).fontSize,
    )
  }

  @Test
  fun changedSettingRoundTripsThroughRepository() = runBlocking {
    val dataStore =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        ) {
          File.createTempFile("roundtrip", ".preferences_pb")
        }
    val repository = DataStoreSettingsRepository(dataStore)

    repository.setAlignment(ListAlignment.Right)

    assertEquals(ListAlignment.Right, repository.settings.first().alignment)
  }

  @Test
  fun roundTripPreservesEveryAlignmentValue() = runBlocking {
    ListAlignment.entries.forEach { alignment ->
      assertEquals(
          alignment,
          appSettingsFrom(preferencesFrom(AppSettings(alignment = alignment))).alignment,
      )
    }
  }

  @Test
  fun outlineSettingRoundTripsThroughRepository() = runBlocking {
    val dataStore =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        ) {
          File.createTempFile("outline-roundtrip", ".preferences_pb")
        }
    val repository = DataStoreSettingsRepository(dataStore)

    repository.setOutlineEnabled(false)
    assertFalse(repository.settings.first().outlineEnabled)

    repository.setOutlineEnabled(true)
    assertTrue(repository.settings.first().outlineEnabled)
  }

  @Test
  fun roundTripPreservesEveryOutlineValue() {
    listOf(true, false).forEach { enabled ->
      assertEquals(
          enabled,
          appSettingsFrom(preferencesFrom(AppSettings(outlineEnabled = enabled))).outlineEnabled,
      )
    }
  }

  @Test
  fun fontSizeSettingRoundTripsThroughRepository() = runBlocking {
    val dataStore =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        ) {
          File.createTempFile("fontsize-roundtrip", ".preferences_pb")
        }
    val repository = DataStoreSettingsRepository(dataStore)

    repository.setFontSize(ListFontSize.Large)

    assertEquals(ListFontSize.Large, repository.settings.first().fontSize)
  }

  @Test
  fun roundTripPreservesEveryFontSizeValue() {
    ListFontSize.entries.forEach { size ->
      assertEquals(
          size,
          appSettingsFrom(preferencesFrom(AppSettings(fontSize = size))).fontSize,
      )
    }
  }
}
