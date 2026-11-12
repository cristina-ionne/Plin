package com.plin.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.*
import androidx.compose.runtime.mutableIntStateOf
import com.plin.ui.theme.PlinColors

private enum class PlinScreen {
    TASK_LIST,
    FRIEND_OF_THE_WEEK_SETTINGS,
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
      
    Column(modifier = Modifier.fillMaxSize()) {
    // 1. Upper Navbar
    NavBar(onProfileClick = { showSettingsOverlay = !showSettingsOverlay })

    // 2. Only Box that contains the rest
    Box(
        modifier = Modifier
        .weight(1f)
        .fillMaxSize()
        .background(PlinColors.PlinWhite)
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
                PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS -> FriendOfTheWeekSettingsScreen(
                    onHome = { screen = PlinScreen.TASK_LIST },
                    onBack = { screen = PlinScreen.TASK_LIST }
                )
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
                onNavigateToCategories = { showSettingsOverlay = false }
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