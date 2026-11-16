package com.plin.data

import android.content.Context

/**
 * Persists Friend of the Week feature settings (enabled flag only).
 * Friend pool lives in Room via [FriendService].
 */
class FriendOfTheWeekSettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, DEFAULT_ENABLED)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    /** Legacy pool stored before Friend entities existed. */
    fun getLegacyFriendPool(): List<String> {
        val raw = prefs.getStringSet(KEY_FRIEND_POOL, emptySet()) ?: emptySet()
        return raw.map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun clearLegacyFriendPool() {
        prefs.edit().remove(KEY_FRIEND_POOL).apply()
    }

    companion object {
        private const val PREFS_NAME = "plin_friend_of_the_week"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_FRIEND_POOL = "friend_pool"
        private const val DEFAULT_ENABLED = true
    }
}
