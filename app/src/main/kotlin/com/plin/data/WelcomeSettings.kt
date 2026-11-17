package com.plin.data

import android.content.Context
import androidx.core.content.edit

class WelcomeSettings(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("plin_welcome_prefs", Context.MODE_PRIVATE)

    /**
     * Checks if welcome overlay has been shown already.
     * Returns true if yes, false if it's the first time.
     */
    fun hasShownWelcome(): Boolean = prefs.getBoolean("has_shown_welcome", false)

    /**
     * Saves the value.
     */
    fun setShownWelcome() {
        prefs.edit { putBoolean("has_shown_welcome", false) }
    }
}