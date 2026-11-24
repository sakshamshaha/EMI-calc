package com.example.emicalculatorapp.data.settings

import android.app.ActivityManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "emiverse_settings")

/**
 * Reactive settings repository powered by Jetpack DataStore Preferences.
 * Includes automatic migration from legacy SharedPreferences ("emiverse_prefs")
 * and hardware-aware default effects level based on ActivityManager.isLowRamDevice.
 */
class SettingsRepository(private val context: Context) {

    private val legacyPrefs = context.getSharedPreferences("emiverse_prefs", Context.MODE_PRIVATE)

    companion object {
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val KEY_EFFECTS_LEVEL = stringPreferencesKey("effects_level")
        val KEY_HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val KEY_NUMBER_FORMAT = stringPreferencesKey("number_format")
    }

    /**
     * Determines whether this device is a low-RAM device (< 2GB RAM or Android Go).
     */
    val isLowRamDevice: Boolean by lazy {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am?.isLowRamDevice == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Hardware-aware default: LITE on low-RAM devices, otherwise BALANCED.
     */
    val defaultEffectsLevel: EffectsLevel
        get() = if (isLowRamDevice) EffectsLevel.LITE else EffectsLevel.BALANCED

    val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        // If not in DataStore yet, fallback to legacy SharedPreferences (so returning users don't see onboarding)
        if (!prefs.contains(KEY_FIRST_LAUNCH)) {
            val legacyFirstLaunch = legacyPrefs.getBoolean("is_first_launch", true)
            legacyFirstLaunch
        } else {
            prefs[KEY_FIRST_LAUNCH] ?: false
        }
    }.distinctUntilChanged()

    val effectsLevelFlow: Flow<EffectsLevel> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_EFFECTS_LEVEL]
        if (raw != null) {
            try {
                EffectsLevel.valueOf(raw)
            } catch (e: IllegalArgumentException) {
                defaultEffectsLevel
            }
        } else {
            defaultEffectsLevel
        }
    }.distinctUntilChanged()

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTICS_ENABLED] ?: true
    }.distinctUntilChanged()

    val numberFormatFlow: Flow<NumberFormatStyle> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_NUMBER_FORMAT]
        if (raw != null) {
            try {
                NumberFormatStyle.valueOf(raw)
            } catch (e: IllegalArgumentException) {
                NumberFormatStyle.INDIAN
            }
        } else {
            NumberFormatStyle.INDIAN
        }
    }.distinctUntilChanged()

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { prefs ->
            prefs[KEY_FIRST_LAUNCH] = false
        }
        // Also keep legacy SharedPreferences in sync
        legacyPrefs.edit().putBoolean("is_first_launch", false).apply()
    }

    suspend fun setEffectsLevel(level: EffectsLevel) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EFFECTS_LEVEL] = level.name
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTICS_ENABLED] = enabled
        }
    }

    suspend fun setNumberFormat(format: NumberFormatStyle) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NUMBER_FORMAT] = format.name
        }
    }
}
