package com.plin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import com.plin.ui.theme.PlinTheme
import androidx.compose.ui.platform.LocalContext
import com.plin.data.FriendOfTheWeekSettingsStore
import com.plin.data.FriendService
import com.plin.data.PlinDatabase
import com.plin.data.TaskService
import com.plin.data.ThemePreferences
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.plin.domain.WeekCalendar
import com.plin.domain.models.Friend
import com.plin.domain.models.Task
import android.app.Application
import com.plin.data.WelcomeSettings

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
fun PlinApp(viewModel: TaskListViewModel = viewModel()) {

    //Variables for the welcome overlay
    val context = LocalContext.current
    val welcomeSettings = remember { WelcomeSettings(context) }
    var showWelcomeOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!welcomeSettings.hasShownWelcome()) {
            showWelcomeOverlay = true
            welcomeSettings.setShownWelcome()
        }
    }

    //variables to change the viewModel on the FriendsScreen
    val application = context.applicationContext as Application
    val friendViewModel: FriendListViewModel = viewModel(
        factory = FriendListViewModel.FriendViewModelFactory(application)
    )
    
    var showSettingsOverlay by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf(PlinScreen.TASK_LIST) }
    // State to manage the currently selected section in the bottom navigation
    var currentBottomSection by remember { mutableStateOf(BottomNavigationSection.Home) }

    val systemInDark = isSystemInDarkTheme()
    val themePrefs = remember { ThemePreferences(context) }

    val pagerState = rememberPagerState(pageCount = { 4 }) // 4 is the number of sections
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val newSection = when (pagerState.currentPage) {
            0 -> BottomNavigationSection.Home
            1 -> BottomNavigationSection.Completed
            2 -> BottomNavigationSection.Friends
            else -> BottomNavigationSection.Rewards
        }
        currentBottomSection = newSection
    }
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

    var showAddOverlay by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    val friends by viewModel.friends.collectAsStateWithLifecycle()

    var showAddFriendOverlay by remember { mutableStateOf(false) }
    var friendToEdit by remember { mutableStateOf<Friend?>(null) }

    PlinTheme(useDarkTheme = isDarkTheme) {

        Box(modifier = Modifier.fillMaxSize()) {
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

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> TaskListScreen(
                                onAddTask = { showAddOverlay = true },
                                onEditTask = { taskToEdit = it }
                            )

                            1 -> CompletedScreen(viewModel)
                            2 -> FriendsScreen(
                                onAddFriend = { showAddFriendOverlay = true },
                                onEditFriend = { friendToEdit = it }
                            )
                            3 -> RewardsScreen(viewModel)
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
                        showSettingsOverlay = false
                        currentBottomSection = section
                        val index = section.toIndex()

                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }

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

                            BottomNavigationSection.Friends -> {
                                screen = PlinScreen.FRIENDS
                            }

                            BottomNavigationSection.Rewards -> {
                                screen = PlinScreen.REWARDS
                            }
                        }
                    }
                )

            }

            //Black background dimming with fade in/out animation
            AnimatedVisibility(
                visible = showWelcomeOverlay || showAddOverlay || taskToEdit != null || showAddFriendOverlay || friendToEdit != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.70f))
                )
            }

            //Welcome overlay with scale and fade in/out animation
            AnimatedVisibility(
                visible = showWelcomeOverlay,
                enter = scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
            ) {
                WelcomeOverlay(
                    onDismiss = { showWelcomeOverlay = false },
                    onToggleFriendOfTheWeek = { isFriendOfTheWeekEnabled }
                )
            }

            //Add task Overlay with scale and fade in/out animation
            AnimatedVisibility(
                visible = showAddOverlay,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
            ) {
                AddTaskOverlay(
                    friends = friends,
                    onDismiss = { showAddOverlay = false },
                    onSave = { title, category, isWeekly, duration, friendIds ->
                        viewModel.addTask(title, category, isWeekly, duration, friendIds)
                        showAddOverlay = false
                    },
                )
            }


            //Edit task Overlay with scale and fade in/out animation
            AnimatedVisibility(
                visible = taskToEdit != null,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
            ) {
                taskToEdit?.let { task ->
                    EditTaskOverlay(
                        task = task,
                        friends = friends,
                        onDismiss = { taskToEdit = null },
                        onSave = { updated ->
                            viewModel.updateTask(updated)
                            taskToEdit = null
                        },
                        onDelete = {
                            viewModel.deleteTask(task)
                            taskToEdit = null
                        },
                    )
                }
            }

            //Add Friend Overlay with scale and fade in/out animation
            AnimatedVisibility(
                visible = showAddFriendOverlay,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AddFriendOverlay(
                        onDismiss = { showAddFriendOverlay = false },
                        onSave = { name, freq ->
                            friendViewModel.addFriend(name, freq)
                            showAddFriendOverlay = false
                        }
                    )
                }
            }

            //Edit Task Overlay with scale and fade in/out animation
            AnimatedVisibility(
                visible = friendToEdit != null,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    friendToEdit?.let { friend ->
                        EditFriendOverlay(
                            friend = friend,
                            onDismiss = { friendToEdit = null },
                            onSave = { updated ->
                                friendViewModel.updateFriend(updated)
                                friendToEdit = null
                            },
                            onDelete = {
                                friendViewModel.deleteFriend(friend)
                                friendToEdit = null
                            }
                        )
                    }
                }
            }


        }
    }
}
