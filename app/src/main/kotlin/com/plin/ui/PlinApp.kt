package com.plin.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableIntStateOf
import com.plin.ui.theme.PlinTheme
import androidx.compose.ui.platform.LocalContext
import com.plin.data.FriendOfTheWeekSettingsStore
import com.plin.data.FriendService
import com.plin.data.PlinDatabase
import com.plin.data.TaskService
import com.plin.data.ThemePreferences
import com.plin.domain.WeekCalendar
import kotlinx.coroutines.launch

private enum class PlinScreen {
    TASK_LIST,
    COMPLETED_SCREEN,
    FRIENDS,
    REWARDS
}

private fun BottomNavigationSection.toIndex(): Int = when (this) {
    BottomNavigationSection.Home -> 0
    BottomNavigationSection.Completed -> 1
    BottomNavigationSection.Friends -> 2
    BottomNavigationSection.Rewards -> 3
}

@Composable
fun PlinApp(viewModel: TaskListViewModel = viewModel()) { //
    
    var showSettingsOverlay by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf(PlinScreen.TASK_LIST) }
    // State to manage the currently selected section in the bottom navigation
    var currentBottomSection by remember { mutableStateOf(BottomNavigationSection.Home) }

    // Status to track previous index
    var previousIndex by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val systemInDark = isSystemInDarkTheme()
    val themePrefs = remember { ThemePreferences(context) }
    val friendOfTheWeekSettingsStore = remember { FriendOfTheWeekSettingsStore(context) }
    val database = remember { PlinDatabase.getInstance(context) }
    val friendService = remember {
        FriendService(
            friendDao = database.friendDao(),
            settingsStore = friendOfTheWeekSettingsStore,
        )
    }
    val taskService = remember { TaskService(database.taskDao()) }
    
    var isDarkTheme by remember { 
        mutableStateOf(
            if (themePrefs.hasPreference()) themePrefs.isDarkMode() 
            else systemInDark
        ) 
    }
    var isFriendOfTheWeekEnabled by remember {
        mutableStateOf(friendOfTheWeekSettingsStore.isEnabled())
    }

    PlinTheme(useDarkTheme = isDarkTheme) {
        Column(modifier = Modifier.fillMaxSize()) {
        // 1. Upper Navbar
        NavBar(
            isDarkTheme = isDarkTheme,
            onProfileClick = { showSettingsOverlay = !showSettingsOverlay })

        // 2. Only Box that contains the rest
        Box(
            modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            ) {
            
            // A. Main content ('when')
            val targetIndex = currentBottomSection.toIndex()
                
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    val direction = if (targetIndex > previousIndex) 1 else -1
                    (slideInHorizontally(initialOffsetX = { it * direction }) + fadeIn()) togetherWith (slideOutHorizontally(targetOffsetX = { it * -direction }) + fadeOut())
                },
                label = "screen_slide"
            ) { targetScreen ->
                when (targetScreen) {
                    PlinScreen.TASK_LIST -> TaskListScreen()
                    PlinScreen.COMPLETED_SCREEN -> CompletedScreen(viewModel)
                    PlinScreen.REWARDS -> RewardsScreen(viewModel)
                    PlinScreen.FRIENDS -> FriendsScreen()
                }
            }

            // B. Animation over the 'when'
            androidx.compose.animation.AnimatedVisibility(
                visible = showSettingsOverlay,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 400) // Slightly increase the duration for a smoother animation
            )
            ) {
                SettingsOverlay(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = {
                        isDarkTheme = it
                        themePrefs.setDarkMode(it)
                    },
                    isFriendOfTheWeekEnabled = isFriendOfTheWeekEnabled,
                    onToggleFriendOfTheWeek = { enabled ->
                        friendOfTheWeekSettingsStore.setEnabled(enabled)
                        isFriendOfTheWeekEnabled = enabled
                        if (enabled) {
                            coroutineScope.launch {
                                taskService.ensureFriendOfTheWeekTask(
                                    week = WeekCalendar.forDate(),
                                    friends = friendService.getAllFriends(),
                                )
                            }
                        }
                    },
                    onNavigateToCategories = { showSettingsOverlay = false },
                )
            }
        }

            // Calling BottomNavigation and passing the current selected section
            BottomNavigation(
                currentSection = currentBottomSection,
                onSectionSelected = { section ->
                    // Update index before changing screen
                    previousIndex = currentBottomSection.toIndex()
                    showSettingsOverlay = false
                    currentBottomSection = section
                    // Manage the action on section change:
                    when (section) {
                        BottomNavigationSection.Home -> {
                            viewModel.selectTab(TaskListTab.CURRENT_WEEK)
                            screen = PlinScreen.TASK_LIST
                        }
                        BottomNavigationSection.Completed -> { 
                            viewModel.selectTab(TaskListTab.ARCHIVE)
                            screen = PlinScreen.COMPLETED_SCREEN
                        }
                        BottomNavigationSection.Friends -> { screen = PlinScreen.FRIENDS }
                        BottomNavigationSection.Rewards -> { screen = PlinScreen.REWARDS }
                    }
                }
            )
        }
    }
}
