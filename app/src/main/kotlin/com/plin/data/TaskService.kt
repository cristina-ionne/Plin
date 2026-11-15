package com.plin.data

import com.plin.domain.WeekCalendar
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Friend
import com.plin.domain.models.Task
import com.plin.domain.models.WeekLayout
import java.time.Instant
import java.time.ZoneId

/**
 * Main entry point the UI uses for tasks.
 *
 * Sits between the front end and the database: screens call this
 * instead of talking to Room / SQLite directly.
 */
class TaskService(private val taskDao: TaskDao) {

    /**
     * Visible list: tasks whose [Task.assignedWeek] is the current week.
     */
    fun observeTasksForWeek(week: WeekLayout) =
        taskDao.observeForAssignedWeek(week.key)

    /** Tasks assigned to weeks before [week] — for the archive tab. */
    fun observeArchiveTasks(week: WeekLayout) =
        taskDao.observeBeforeWeek(week.key)

    suspend fun addTask(
        title: String,
        category: TaskCategory,
        isWeekly: Boolean = false,
        assignedWeek: String,
        duration: Int,
        friendIds: List<String> = emptyList(),
    ): Long {
        val finalTitle = if (title.isBlank()) "Task name" else title.trim()
        val finalDuration = if (duration <= 0) 1 else duration
        val linkedFriendIds =
            if (category == TaskCategory.SOCIAL_ACTIVITY) {
                friendIds.filter { it.isNotBlank() }.distinct()
            } else {
                emptyList()
            }

        require(assignedWeek.isNotEmpty()) { "assignedWeek is required" }

        val taskId = taskDao.getNextTaskId()
        taskDao.insert(
            Task(
                id = taskId,
                duration = finalDuration,
                instanceNumber = 0,
                title = finalTitle,
                category = category,
                points = Task.pointsFromDuration(finalDuration),
                isWeekly = isWeekly,
                assignedWeek = assignedWeek,
                friendIds = linkedFriendIds,
            ),
        )
        return taskId
    }

    /**
     * Creates or backfills a one-shot Friend of the Week Social Activity for [week]
     * (not a rolling weekly series), assigning a random friend when available.
     */
    suspend fun ensureFriendOfTheWeekTask(week: WeekLayout, friends: List<Friend>) {
        val existing = taskDao.getFriendOfTheWeekForWeek(week.key)
        val picked = friends.randomOrNull()

        if (existing == null) {
            val taskId = taskDao.getNextTaskId()
            taskDao.insert(
                Task.createWeeklySocialFriendTask(
                    taskId = taskId,
                    weekKey = week.key,
                    friend = picked,
                ),
            )
            return
        }

        // Backfill friend + title when still on the empty auto pattern.
        val canBackfill =
            !existing.hasLinkedFriends() &&
                picked != null &&
                (existing.title == Task.FOTW_FALLBACK_TITLE || existing.title.startsWith(CONNECT_WITH_PREFIX))

        if (canBackfill) {
            taskDao.update(
                existing.copy(
                    friendIds = listOf(picked.id),
                    title = Task.connectWithTitle(picked.name),
                ),
            )
            return
        }

        // Migrated rows may already have a friend id but still use the fallback title.
        if (existing.title == Task.FOTW_FALLBACK_TITLE && existing.hasLinkedFriends()) {
            val linked = friends.firstOrNull { it.id == existing.friendIds.first() }
            if (linked != null) {
                taskDao.update(
                    existing.copy(title = Task.connectWithTitle(linked.name)),
                )
            }
        }
    }

    /**
     * Fills [Task.assignedWeek] for rows migrated without it (from [Task.createdAt]).
     */
    suspend fun backfillAssignedWeeksIfNeeded() {
        val missing = taskDao.getTasksMissingAssignedWeek()
        val zone = ZoneId.systemDefault()
        for (task in missing) {
            if (isAutoFriendOfTheWeekTask(task)) {
                continue
            }
            val date = Instant.ofEpochMilli(task.createdAt).atZone(zone).toLocalDate()
            val weekKey = WeekCalendar.forDate(date).key
            taskDao.update(task.copy(assignedWeek = weekKey))
        }
    }

    /**
     * On a new calendar week:
     * - Weekly: copy tasks tagged weekly from the immediate previous week into [currentWeek]
     * - Non-weekly incomplete: bump [Task.assignedWeek] to [currentWeek] so they keep showing
     */
    suspend fun applyNewWeek(currentWeek: WeekLayout) {
        rollWeeklyTasksIntoNewWeek(currentWeek)
        taskDao.carryIncompleteNonWeeklyToWeek(currentWeek.key)
    }

    private suspend fun rollWeeklyTasksIntoNewWeek(currentWeek: WeekLayout) {
        val previousWeek = WeekCalendar.forDate(currentWeek.startDate.minusDays(7))
        val previousWeeklyTasks = taskDao.getWeeklyTasksForWeek(previousWeek.key)

        for (previous in previousWeeklyTasks) {
            val alreadyInCurrentWeek = taskDao.getInstanceForWeek(previous.id, currentWeek.key)
            if (alreadyInCurrentWeek != null) {
                continue
            }

            taskDao.insert(
                Task(
                    id = previous.id,
                    duration = previous.duration,
                    instanceNumber = previous.instanceNumber + 1,
                    title = previous.title,
                    category = previous.category,
                    points = Task.pointsFromDuration(previous.duration),
                    isWeekly = true,
                    completed = false,
                    assignedWeek = currentWeek.key,
                    friendIds = previous.friendIds,
                ),
            )
        }
    }

    suspend fun setCompleted(task: Task, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        taskDao.update(task.copy(completed = completed, completedAt = completedAt))
    }

    suspend fun toggleCompletion(task: Task) {
        setCompleted(task, !task.completed)
    }

    suspend fun updateTask(task: Task) {
        val trimmed = task.title.trim()
        require(trimmed.isNotEmpty()) { "title is required" }
        val linkedFriendIds =
            if (task.category == TaskCategory.SOCIAL_ACTIVITY) {
                task.friendIds.filter { it.isNotBlank() }.distinct()
            } else {
                emptyList()
            }
        taskDao.update(
            task.copy(
                title = trimmed,
                friendIds = linkedFriendIds,
                points = Task.pointsFromDuration(task.duration),
            ),
        )
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    private fun isAutoFriendOfTheWeekTask(task: Task): Boolean =
        task.category == TaskCategory.SOCIAL_ACTIVITY &&
            !task.isWeekly &&
            (task.title == Task.FOTW_FALLBACK_TITLE || task.title.startsWith(CONNECT_WITH_PREFIX))

    companion object {
        private const val CONNECT_WITH_PREFIX = "Connect with "
    }
}
