package com.plin.ui

import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically


private enum class PlinScreen {
    TASK_LIST,
    FRIEND_OF_THE_WEEK_SETTINGS,
}

@Composable
fun PlinApp() {
    // State to manage the overlay when the settings button is clicked
    var showSettingsOverlay by remember { mutableStateOf(false) }

     // Pre-existing state for the main screen:
    var screen by remember { mutableStateOf(PlinScreen.TASK_LIST) }
    
    // State to manage the currently selected section in the bottom navigation
    var currentBottomSection by remember { mutableStateOf(BottomNavigationSection.Home) }
      
    Column(modifier = Modifier.fillMaxSize()) {
    // 1. Upper Navbar
    NavBar(onProfileClick = { showSettingsOverlay = !showSettingsOverlay })

    // 2. Only Box that contains the rest
    Box(modifier = Modifier.weight(1f)) {
        
        // A. Main content ('when')
        when (screen) {
            PlinScreen.TASK_LIST -> TaskListScreen(
                // First you had: onOpenSettings = { screen = PlinScreen.SETTINGS },
                // Now you can use the overlay toggle:
                onOpenSettings = { showSettingsOverlay = !showSettingsOverlay },
)
            PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS -> FriendOfTheWeekSettingsScreen(onHome = { screen = PlinScreen.TASK_LIST },
                    // First you had: onBack = { screen = PlinScreen.SETTINGS },
                    // Now you go back to the list (or to the overlay, depending on what you prefer):
                    onBack = { screen = PlinScreen.TASK_LIST },)
        }

        // B. Animation over the 'when'
        androidx.compose.animation.AnimatedVisibility(
            visible = showSettingsOverlay,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(
            targetOffsetY = { fullHeight -> 
                // Instead of -fullHeight (that stops the upper border exactly at 0),
                // we use a larger value to make it disappear completely above the screen.
                // -fullHeight - 100 will ensure the base of the overlay reaches outside the screen.
                -fullHeight - 200
            },
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
                    BottomNavigationSection.Home -> screen = PlinScreen.TASK_LIST
                    BottomNavigationSection.Completed -> { /* Navigate to Completed screen */ }
                    BottomNavigationSection.Friends -> { /* Navigate to Friends screen */ }
                    BottomNavigationSection.Rewards -> { /* Navigate to Rewards screen */ }
                }
            }
        )
    }
        
}