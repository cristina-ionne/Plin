package com.plin.domain.models

import androidx.room.Entity
import com.plin.domain.enums.TaskCategory

/**
 * A single to-do item in the Plin task list.
 *
 * [id] identifies the task across weekly versions; [instanceNumber] starts at 0
 * and increments when a new weekly copy is generated.
 * [assignedWeek] is the ISO week key (e.g. 2026-W34) this instance belongs to.
 *
 * Social Activity tasks may link zero or more friends via [friendIds].
 */
@Entity(tableName = "tasks", primaryKeys = ["id", "instanceNumber"])
data class Task(
    val id: Long,
    val duration: Int,
    val instanceNumber: Int = 0,
    val title: String,
    val completed: Boolean = false,
    val category: TaskCategory = TaskCategory.GENERIC,
    val points: Int = 1,
    val isWeekly: Boolean = false,
    val assignedWeek: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val friendIds: List<String> = emptyList(),
) {
    fun hasLinkedFriends(): Boolean = friendIds.isNotEmpty()

    companion object {
        const val FOTW_FALLBACK_TITLE = "Friend of the Week"
        private const val TITLE_PREFIX = "Connect with "
        private const val FOTW_DEFAULT_DURATION = 1
        /** UI stores "<1 hour" as duration code 6. */
        private const val DURATION_LESS_THAN_ONE_HOUR = 6

        fun connectWithTitle(friendName: String?): String {
            val trimmed = friendName?.trim().orEmpty()
            return if (trimmed.isEmpty()) FOTW_FALLBACK_TITLE else "$TITLE_PREFIX$trimmed"
        }

        /**
         * Maps task duration to points: &lt;1h → 1, 1h → 2, 2h → 3, …, 5+h → 6.
         */
        fun pointsFromDuration(duration: Int): Int =
            when {
                duration == DURATION_LESS_THAN_ONE_HOUR -> 1
                duration <= 0 -> 2
                else -> duration + 1
            }

        /**
         * One-shot Friend of the Week for [weekKey] — not rolled by weekly regeneration.
         */
        fun createWeeklySocialFriendTask(taskId: Long, weekKey: String, friend: Friend?): Task {
            val friendIds = if (friend != null) listOf(friend.id) else emptyList()
            return Task(
                id = taskId,
                duration = FOTW_DEFAULT_DURATION,
                instanceNumber = 0,
                title = connectWithTitle(friend?.name),
                category = TaskCategory.SOCIAL_ACTIVITY,
                points = pointsFromDuration(FOTW_DEFAULT_DURATION),
                isWeekly = false,
                assignedWeek = weekKey,
                friendIds = friendIds,
            )
        }
    }
}
