package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yawmi_preferences")

data class UserPreferences(
    val language: String = "ar", // "ar" for Arabic, "en" for English
    val themeMode: String = "dark", // Signature Dark Theme from launcher
    val fontScaleMode: String = "normal", // "small" (0.95), "medium" (1.05), "normal" (1.15), "large" (1.30)
    val notificationsEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val basicSalary: Double = 0.0,
    val addedSalary: Double = 0.0,
    val addedSalaryName: String = "",
    val previousMonthBalance: Double = 0.0,
    val salaryDayOfMonth: Int = 1
) {
    val fontScale: Float
        get() = when (fontScaleMode) {
            "extra_small" -> 0.85f
            "small" -> 0.92f
            "normal", "system" -> 1.0f
            "medium" -> 1.08f
            "large" -> 1.18f
            "extra_large" -> 1.28f
            else -> 1.0f
        }

    val totalIncome: Double
        get() = basicSalary + addedSalary + previousMonthBalance
}

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_FONT_SCALE_MODE = stringPreferencesKey("font_scale_mode")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_VIBRATE = booleanPreferencesKey("vibrate_enabled")
        val KEY_BASIC_SALARY = doublePreferencesKey("basic_salary")
        val KEY_ADDED_SALARY = doublePreferencesKey("added_salary")
        val KEY_ADDED_SALARY_NAME = stringPreferencesKey("added_salary_name")
        val KEY_PREVIOUS_MONTH_BALANCE = doublePreferencesKey("previous_month_balance")
        val KEY_SALARY_DAY_OF_MONTH = androidx.datastore.preferences.core.intPreferencesKey("salary_day_of_month")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            language = preferences[KEY_LANGUAGE] ?: "ar",
            themeMode = preferences[KEY_THEME] ?: "dark",
            fontScaleMode = preferences[KEY_FONT_SCALE_MODE] ?: "normal",
            notificationsEnabled = preferences[KEY_NOTIFICATIONS] ?: true,
            vibrateEnabled = preferences[KEY_VIBRATE] ?: true,
            basicSalary = preferences[KEY_BASIC_SALARY] ?: 0.0,
            addedSalary = preferences[KEY_ADDED_SALARY] ?: 0.0,
            addedSalaryName = preferences[KEY_ADDED_SALARY_NAME] ?: "",
            previousMonthBalance = preferences[KEY_PREVIOUS_MONTH_BALANCE] ?: 0.0,
            salaryDayOfMonth = preferences[KEY_SALARY_DAY_OF_MONTH] ?: 1
        )
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = mode
        }
    }

    suspend fun setFontScaleMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FONT_SCALE_MODE] = mode
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setVibrateEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VIBRATE] = enabled
        }
    }

    suspend fun setBasicSalary(salary: Double) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BASIC_SALARY] = salary
        }
    }

    suspend fun setAddedSalary(added: Double) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ADDED_SALARY] = added
        }
    }

    suspend fun setAddedSalaryName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ADDED_SALARY_NAME] = name
        }
    }

    suspend fun setPreviousMonthBalance(balance: Double) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PREVIOUS_MONTH_BALANCE] = balance
        }
    }

    suspend fun setSalaryDayOfMonth(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SALARY_DAY_OF_MONTH] = day
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
