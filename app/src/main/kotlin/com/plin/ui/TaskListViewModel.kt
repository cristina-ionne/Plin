package com.plin.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plin.data.FriendOfTheWeekSettingsStore
import com.plin.data.PlinDatabase
import com.plin.data.TaskService
import com.plin.data.WeekService
import com.plin.data.WeekStateStore
import com.plin.data.routines.GenerateWeeklyTasksRoutine
import com.plin.domain.WeekCalendar
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Task
import com.plin.domain.models.WeekLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

/**
 * Holds UI state for the task list and talks to [TaskService].
 * On start, resolves the current week and runs weekly routines if needed.
 */
class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    //To give the same height to all the welcome cards
    var todayCardHeight by mutableStateOf<Int?>(null)

    private val taskService = TaskService(
        PlinDatabase.getInstance(application).taskDao(),
    )
    private val friendOfTheWeekSettingsStore = FriendOfTheWeekSettingsStore(application)
    private val weekService = WeekService(
        weekStateStore = WeekStateStore(application),
        routines = listOf(GenerateWeeklyTasksRoutine(taskService)),
        taskService = taskService,
    )

    private val _currentWeek = MutableStateFlow<WeekLayout?>(null)

    private val _selectedTab = MutableStateFlow(TaskListTab.CURRENT_WEEK)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(_currentWeek, _selectedTab) { week, tab ->
        week to tab
    }
        .flatMapLatest { (week, tab) ->
            when {
                week == null -> flowOf(emptyList())
                tab == TaskListTab.ARCHIVE -> taskService.observeArchiveTasks(week)
                else -> taskService.observeTasksForWeek(week)
            }
        }
        
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
        
    //Calculate total points
    val totalPoints: StateFlow<Int> = tasks.map { taskList ->
        taskList.filter { it.completed }.sumOf { it.points }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    fun refreshMainView() {
        viewModelScope.launch {
            _currentWeek.value = weekService.onAppOpen()
            maybeEnsureFriendOfTheWeek()
        }
    }

    private suspend fun maybeEnsureFriendOfTheWeek() {
        if (!friendOfTheWeekSettingsStore.isEnabled()) {
            return
        }
        val week = _currentWeek.value ?: return
        taskService.ensureFriendOfTheWeekTask(week)
    }

    fun selectTab(tab: TaskListTab) {
        _selectedTab.value = tab
    }

    fun addTask(title: String, category: TaskCategory, isWeekly: Boolean, duration: Int) {
        //Empty title is not allowed, so we set a default title if the user doesn't provide one
        val finalTitle = title.ifBlank { "Task name" }
        //Empty duration is not allowed, so we set a default duration if the user doesn't provide one
        val finalDuration = if (duration <= 0) 1 else duration

        viewModelScope.launch {
            val weekKey = _currentWeek.value?.key ?: WeekCalendar.forDate().key
            taskService.addTask(title = finalTitle, category = category, isWeekly = isWeekly, assignedWeek = weekKey, duration = finalDuration)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskService.toggleCompletion(task)
        }
    }

    fun setCompleted(task: Task, completed: Boolean) {
        viewModelScope.launch {
            taskService.setCompleted(task, completed)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskService.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskService.deleteTask(task)
        }
    }
}
