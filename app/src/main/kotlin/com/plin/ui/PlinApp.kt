package com.plin.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier


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
        
        NavBar(onProfileClick = { showSettingsOverlay = !showSettingsOverlay })

        Box(modifier = Modifier.weight(1f)) {
            
            when (screen) {
                PlinScreen.TASK_LIST -> TaskListScreen(
                // First you had: onOpenSettings = { screen = PlinScreen.SETTINGS },
                // Now you can use the overlay toggle:
                onOpenSettings = { showSettingsOverlay = !showSettingsOverlay },

                )
                
                PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS -> FriendOfTheWeekSettingsScreen(
                     onHome = { screen = PlinScreen.TASK_LIST },
                    // First you had: onBack = { screen = PlinScreen.SETTINGS },
                    // Now you go back to the list (or to the overlay, depening on what you prefer):
                    onBack = { screen = PlinScreen.TASK_LIST },
        
                )
            }

            if (showSettingsOverlay) {
                    SettingsOverlay(
                        onDismiss = { showSettingsOverlay = false },
                        onNavigateToCategories = {
                            showSettingsOverlay = false
                        }
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