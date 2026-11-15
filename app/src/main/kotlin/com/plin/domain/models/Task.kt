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
 * Regular tasks use [title] directly. Friend of the Week rows use [friendId] plus
 * [displayTitle] with a name from the friends pool.
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
    val friendId: String = "",
) {
    val isFriendOfTheWeek: Boolean
        get() = category == TaskCategory.FRIEND_OF_THE_WEEK

    val effectiveIsWeekly: Boolean
        get() = isFriendOfTheWeek || isWeekly

    fun hasAssignedFriend(): Boolean = friendId.isNotEmpty()

    fun displayTitle(friendName: String = ""): String =
        if (isFriendOfTheWeek) displayTitleForFriend(friendName) else title

    companion object {
        const val FOTW_FALLBACK_TITLE = "Friend of the Week"
        private const val TITLE_PREFIX = "Connect with "
        private const val FOTW_DEFAULT_DURATION = 1

        fun displayTitleForFriend(friendName: String): String =
            if (friendName.isBlank()) FOTW_FALLBACK_TITLE else "$TITLE_PREFIX$friendName"

        fun createFriendOfWeek(taskId: Long, weekKey: String, friend: Friend?): Task = Task(
            id = taskId,
            duration = FOTW_DEFAULT_DURATION,
            instanceNumber = 0,
            title = FOTW_FALLBACK_TITLE,
            category = TaskCategory.FRIEND_OF_THE_WEEK,
            isWeekly = true,
            assignedWeek = weekKey,
            friendId = friend?.id.orEmpty(),
        )
    }
}
