package com.plin.data

import android.content.Context
import androidx.core.content.edit

class ThemePreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("plin_prefs", Context.MODE_PRIVATE)

    fun hasPreference(): Boolean {
    return prefs.contains("is_dark_mode")
}

    fun isFriendOfTheWeekEnabled(): Boolean = prefs.getBoolean("friend_of_the_week_enabled", true)

    fun setFriendOfTheWeekEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("friend_of_the_week_enabled", enabled) }
    }

fun isDarkMode(): Boolean {
    return prefs.getBoolean("is_dark_mode", false)
}

    fun setDarkMode(enabled: Boolean) {
        prefs.edit {putBoolean("is_dark_mode", enabled)}
    }
}