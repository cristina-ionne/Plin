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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

private enum class PlinScreen {
    TASK_LIST,
    FRIEND_OF_THE_WEEK_SETTINGS,
    COMPLETED_SCREEN,
    FRIENDS,
    REWARDS
}

@Composable
fun PlinApp(viewModel: TaskListViewModel = viewModel()) { //
    
    var showSettingsOverlay by remember { mutableStateOf(false) }

    var screen by remember { mutableStateOf(PlinScreen.TASK_LIST) }
    
    // State to manage the currently selected section in the bottom navigation
    var currentBottomSection by remember { mutableStateOf(BottomNavigationSection.Home) }
      
    Column(modifier = Modifier.fillMaxSize()) {
    // 1. Upper Navbar
    NavBar(onProfileClick = { showSettingsOverlay = !showSettingsOverlay })

    // 2. Only Box that contains the rest
    Box(
        modifier = Modifier
        .weight(1f)
        .fillMaxSize()
        .background(Color(0xFFF0FAFF))
        ) {
        
        // A. Main content ('when')
        when (screen) {
            PlinScreen.TASK_LIST -> TaskListScreen()
            PlinScreen.COMPLETED_SCREEN -> CompletedScreen(viewModel)
            PlinScreen.REWARDS -> RewardsScreen(viewModel)
            PlinScreen.FRIENDS -> FriendsScreen()
            PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS -> FriendOfTheWeekSettingsScreen(onHome = { screen = PlinScreen.TASK_LIST },
                    // First you had: onBack = { screen = PlinScreen.SETTINGS },
                    // Now you go back to the list (or to the overlay, depending on what you prefer):
                    onBack = { screen = PlinScreen.TASK_LIST },)
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
                onDismiss = { showSettingsOverlay = false },
                onNavigateToCategories = { showSettingsOverlay = false }
            )
        }
    }

         // Calling BottomNavigation and passing the current selected section
        BottomNavigation(
            currentSection = currentBottomSection,
            onSectionSelected = { section ->
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