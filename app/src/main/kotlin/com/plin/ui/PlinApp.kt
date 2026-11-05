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
    SETTINGS,
    FRIEND_OF_THE_WEEK_SETTINGS,
}

@Composable
fun PlinApp() {
     // Pre-existing state for the main screen:
    var screen by remember { mutableStateOf(PlinScreen.TASK_LIST) }
    
    // NEW DECLARATION:
    var currentBottomSection by remember { mutableStateOf(BottomNavigationSection.Home) }
      
    Column(modifier = Modifier.fillMaxSize()) {
        NavBar(onProfileClick = { screen = PlinScreen.SETTINGS })

        Box(modifier = Modifier.weight(1f)) {
            when (screen) {
                PlinScreen.TASK_LIST -> TaskListScreen(
                    onOpenSettings = { screen = PlinScreen.SETTINGS },
                )
                PlinScreen.SETTINGS -> SettingsScreen(
                    onHome = { screen = PlinScreen.TASK_LIST },
                    onBack = { screen = PlinScreen.TASK_LIST },
                    onOpenFriendOfTheWeek = { screen = PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS },
                )
                PlinScreen.FRIEND_OF_THE_WEEK_SETTINGS -> FriendOfTheWeekSettingsScreen(
                    onHome = { screen = PlinScreen.TASK_LIST },
                    onBack = { screen = PlinScreen.SETTINGS },
                )
            }
        }
        
         // Calling BottomNavigation and passing the current selected section
        BottomNavigation(
            currentSection = currentBottomSection,
            onSectionSelected = { section ->
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