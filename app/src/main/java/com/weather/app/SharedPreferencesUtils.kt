package com.weather.app

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {
    private const val NAME = "settings"
    private const val TEMPERATURE_UNIT_KEY = "temperature_unit"
    private const val THEME_KEY = "theme"

    private const val DEFAULT_TEMPERATURE_UNIT = "celsius"
    private const val DEFAULT_THEME = "auto"

    fun saveTemperatureUnit(context: Context, temperatureUnit: String) {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(TEMPERATURE_UNIT_KEY, temperatureUnit).apply()
    }

    fun getTemperatureUnit(context: Context): String {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return prefs.getString(TEMPERATURE_UNIT_KEY, DEFAULT_TEMPERATURE_UNIT)
            ?: DEFAULT_TEMPERATURE_UNIT
    }

    fun saveTheme(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(THEME_KEY, theme).apply()
    }

    fun getTheme(context: Context): String {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return prefs.getString(THEME_KEY, DEFAULT_THEME) ?: DEFAULT_THEME
    }

    fun registerOnSharedPreferenceChangeListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        val prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }
}
