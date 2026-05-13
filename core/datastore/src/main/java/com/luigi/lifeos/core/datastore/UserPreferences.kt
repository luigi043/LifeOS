package com.luigi.lifeos.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "lifeos_prefs")

/**
 * Lightweight user preferences. NOT for sensitive data — use EncryptedFile
 * or DataStore + crypto for tokens, health logs, etc.
 *
 * Keys defined here are global. Feature-specific prefs (e.g. last-selected
 * workout split, default expense category) should live in feature modules
 * with their own DataStore instance to avoid coupling.
 */
@Singleton
class UserPreferences @Inject constructor(
    private val context: Context,
) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val BASE_CURRENCY = stringPreferencesKey("base_currency")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val data: Flow<Preferences> = context.dataStore.data

    val darkMode: Flow<Boolean?> = data.map { it[DARK_MODE] }
    val baseCurrency: Flow<String> = data.map { it[BASE_CURRENCY] ?: "EUR" }
    val onboardingDone: Flow<Boolean> = data.map { it[ONBOARDING_DONE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun setBaseCurrency(code: String) {
        context.dataStore.edit { it[BASE_CURRENCY] = code }
    }

    suspend fun markOnboardingDone() {
        context.dataStore.edit { it[ONBOARDING_DONE] = true }
    }
}
