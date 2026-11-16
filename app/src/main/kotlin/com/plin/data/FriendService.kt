package com.plin.data

import com.plin.domain.models.Friend

/**
 * CRUD for the Friend of the Week pool.
 */
class FriendService(
    private val friendDao: FriendDao,
    private val settingsStore: FriendOfTheWeekSettingsStore,
) {
    fun observeFriends() = friendDao.observeAll()

    suspend fun getAllFriends(): List<Friend> = friendDao.getAll()

    /**
     * One-time import of legacy string pool from SharedPreferences into Room.
     */
    suspend fun migrateFromPrefsIfNeeded() {
        val legacyNames = settingsStore.getLegacyFriendPool()
        if (legacyNames.isEmpty()) {
            return
        }
        if (friendDao.getAll().isNotEmpty()) {
            settingsStore.clearLegacyFriendPool()
            return
        }
        for (name in legacyNames) {
            addFriend(name = name, frequency = DEFAULT_LEGACY_FREQUENCY)
        }
        settingsStore.clearLegacyFriendPool()
    }

    suspend fun addFriend(name: String, frequency: Int): Friend {
        val friend = Friend.create(name = name, frequency = frequency)
        require(friend.name.isNotEmpty()) { "name is required" }
        require(frequency in MIN_FREQUENCY..MAX_FREQUENCY) { "frequency must be between $MIN_FREQUENCY and $MAX_FREQUENCY" }
        friendDao.insert(friend)
        return friend
    }

    suspend fun updateFriend(friend: Friend) {
        val trimmed = friend.name.trim()
        require(trimmed.isNotEmpty()) { "name is required" }
        require(friend.frequency in MIN_FREQUENCY..MAX_FREQUENCY) {
            "frequency must be between $MIN_FREQUENCY and $MAX_FREQUENCY"
        }
        friendDao.update(friend.copy(name = trimmed))
    }

    suspend fun removeFriend(friend: Friend) {
        friendDao.delete(friend)
    }

    companion object {
        private const val MIN_FREQUENCY = 1
        private const val MAX_FREQUENCY = 5
        private const val DEFAULT_LEGACY_FREQUENCY = 3
    }
}
