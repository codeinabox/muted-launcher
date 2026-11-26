package io.github.codeinabox.mutedlauncher.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Persists [AppSettings]; changes are exposed as a [Flow] so consumers re-render immediately. */
interface SettingsRepository {
  val settings: Flow<AppSettings>

  suspend fun setAlignment(alignment: ListAlignment)

  suspend fun setOutlineEnabled(enabled: Boolean)

  suspend fun setFontSize(size: ListFontSize)
}

/** DataStore Preferences key for [AppSettings.alignment]. */
const val KEY_ALIGNMENT = "alignment"

/** DataStore Preferences key for [AppSettings.outlineEnabled]. */
const val KEY_OUTLINE_ENABLED = "outline_enabled"

/** DataStore Preferences key for [AppSettings.fontSize]. */
const val KEY_FONT_SIZE = "font_size"

/**
 * Maps stored preferences to [AppSettings], defaulting to [AppSettings] when a key is absent or
 * holds an unknown value. Exposed (internal) for JVM tests of the key mapping.
 */
internal fun appSettingsFrom(preferences: Preferences): AppSettings =
    AppSettings(
        alignment =
            preferences[stringPreferencesKey(KEY_ALIGNMENT)]?.let { stored ->
              ListAlignment.entries.firstOrNull { it.name == stored }
            } ?: ListAlignment.Left,
        outlineEnabled = preferences[booleanPreferencesKey(KEY_OUTLINE_ENABLED)] ?: true,
        fontSize =
            preferences[stringPreferencesKey(KEY_FONT_SIZE)]?.let { stored ->
              ListFontSize.entries.firstOrNull { it.name == stored }
            } ?: ListFontSize.Medium,
    )

/**
 * Maps [AppSettings] to stored preferences. Exposed (internal) for JVM tests of the key mapping.
 */
internal fun preferencesFrom(settings: AppSettings): Preferences =
    preferencesOf(
        stringPreferencesKey(KEY_ALIGNMENT) to settings.alignment.name,
        booleanPreferencesKey(KEY_OUTLINE_ENABLED) to settings.outlineEnabled,
        stringPreferencesKey(KEY_FONT_SIZE) to settings.fontSize.name,
    )

/**
 * [SettingsRepository] backed by DataStore Preferences in the default `files/datastore/` location.
 */
class DataStoreSettingsRepository(private val dataStore: DataStore<Preferences>) :
    SettingsRepository {
  override val settings: Flow<AppSettings> = dataStore.data.map(::appSettingsFrom)

  override suspend fun setAlignment(alignment: ListAlignment) {
    dataStore.edit { it[stringPreferencesKey(KEY_ALIGNMENT)] = alignment.name }
  }

  override suspend fun setOutlineEnabled(enabled: Boolean) {
    dataStore.edit { it[booleanPreferencesKey(KEY_OUTLINE_ENABLED)] = enabled }
  }

  override suspend fun setFontSize(size: ListFontSize) {
    dataStore.edit { it[stringPreferencesKey(KEY_FONT_SIZE)] = size.name }
  }
}

/** Shared DataStore instance; the default location keeps settings under Auto Backup. */
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
