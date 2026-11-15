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
        friendId: String = "",
    ): Long {
        val finalTitle = if (title.isBlank()) "Task name" else title.trim()
        val finalDuration = if (duration <= 0) 1 else duration

        require(assignedWeek.isNotEmpty()) { "assignedWeek is required" }

        val taskId = taskDao.getNextTaskId()
        taskDao.insert(
            Task(
                id = taskId,
                duration = finalDuration,
                instanceNumber = 0,
                title = finalTitle,
                category = category,
                isWeekly = isWeekly,
                assignedWeek = assignedWeek,
                friendId = friendId,
            ),
        )
        return taskId
    }

    /**
     * Creates or backfills the weekly Friend of the Week task for [week],
     * assigning a random friend from [friends] when available.
     */
    suspend fun ensureFriendOfTheWeekTask(week: WeekLayout, friends: List<Friend>) {
        val existing = taskDao.getFriendOfTheWeekForWeek(week.key)
        val picked = friends.randomOrNull()

        if (existing == null) {
            val taskId = taskDao.getNextTaskId()
            taskDao.insert(
                Task.createFriendOfWeek(taskId = taskId, weekKey = week.key, friend = picked),
            )
            return
        }

        if (!existing.hasAssignedFriend() && picked != null) {
            taskDao.update(
                existing.copy(friendId = picked.id),
            )
        }
    }

    /**
     * Fills [Task.assignedWeek] for rows migrated without it (from [Task.createdAt]).
     */
    suspend fun backfillAssignedWeeksIfNeeded() {
        val missing = taskDao.getTasksMissingAssignedWeek()
        val zone = ZoneId.systemDefault()
        for (task in missing) {
            if (task.isFriendOfTheWeek) {
                continue
            }
            val date = Instant.ofEpochMilli(task.createdAt).atZone(zone).toLocalDate()
            val weekKey = WeekCalendar.forDate(date).key
            taskDao.update(task.copy(assignedWeek = weekKey))
        }
    }

    /**
     * On a new calendar week:
     * - Weekly: create a new incomplete instance for [currentWeek]
     * - Non-weekly incomplete: bump [Task.assignedWeek] to [currentWeek] so they keep showing
     */
    suspend fun applyNewWeek(currentWeek: WeekLayout) {
        rollWeeklyTasksIntoNewWeek(currentWeek)
        taskDao.carryIncompleteNonWeeklyToWeek(currentWeek.key)
    }

    private suspend fun rollWeeklyTasksIntoNewWeek(currentWeek: WeekLayout) {
        val taskIds = taskDao.getWeeklyTaskIds()
        for (taskId in taskIds) {
            val alreadyInCurrentWeek = taskDao.getInstanceForWeek(taskId, currentWeek.key)
            if (alreadyInCurrentWeek != null) {
                continue
            }

            val latest = taskDao.getLatestForTask(taskId) ?: continue
            if (latest.isFriendOfTheWeek) {
                continue
            }
            taskDao.insert(
                Task(
                    id = taskId,
                    duration = latest.duration,
                    instanceNumber = latest.instanceNumber + 1,
                    title = latest.title,
                    category = latest.category,
                    points = latest.points,
                    isWeekly = true,
                    completed = false,
                    assignedWeek = currentWeek.key,
                    friendId = latest.friendId,
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
        taskDao.update(task.copy(title = trimmed))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }
}
