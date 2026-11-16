package com.plin.data

import androidx.room.TypeConverter
import com.plin.domain.enums.TaskCategory

/**
 * Teaches Room how to store types SQLite does not support natively.
 */
class Converters {
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory =
        when (value) {
            "FRIEND_OF_THE_WEEK" -> TaskCategory.SOCIAL_ACTIVITY
            else -> TaskCategory.valueOf(value)
        }

    @TypeConverter
    fun fromFriendIds(friendIds: List<String>): String =
        friendIds.filter { it.isNotBlank() }.joinToString(separator = ",")

    @TypeConverter
    fun toFriendIds(value: String): List<String> =
        if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
}
