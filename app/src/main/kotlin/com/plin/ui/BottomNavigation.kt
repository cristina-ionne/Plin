package com.plin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.plin.R
import com.plin.ui.theme.FustatFamily

enum class BottomNavigationSection { Completed, Friends, Rewards, Home }
enum class TodayIconSelected { On, Off }

// Defining the navbar icons

@Composable
fun TodayIcon(modifier: Modifier = Modifier, selected: TodayIconSelected = TodayIconSelected.Off) {
    val isSelected = selected == TodayIconSelected.On
    Icon(
        painter = painterResource(id = if (isSelected) R.drawable.ic_today_on else R.drawable.ic_today),
        contentDescription = "Today",
        tint = (if (isSelected) Color.Unspecified else MaterialTheme.colorScheme.onSurface),
        modifier = modifier
    )
}

@Composable
fun CompletedIcon(modifier: Modifier = Modifier, selected: Boolean = false) {
    Icon(
        painter = painterResource(id = if (selected) R.drawable.ic_completed_on else R.drawable.ic_completed),
        contentDescription = "Completed",
        tint = (if (selected) Color.Unspecified else MaterialTheme.colorScheme.onSurface),
        modifier = modifier
    )
}

@Composable
fun FriendsIcon(modifier: Modifier = Modifier, selected: Boolean = false) {
    Icon(
        painter = painterResource(id = if (selected) R.drawable.ic_friends_on else R.drawable.ic_friends),
        contentDescription = "Friends",
        tint = (if (selected) Color.Unspecified else MaterialTheme.colorScheme.onSurface),
        modifier = modifier
    )
}

@Composable
fun RewardsIcon(modifier: Modifier = Modifier, selected: Boolean = false) {
    Icon(
        painter = painterResource(id = if (selected) R.drawable.ic_rewards_on else R.drawable.ic_rewards),
        contentDescription = "Rewards",
        tint = (if (selected) Color.Unspecified else MaterialTheme.colorScheme.onSurface),
        modifier = modifier
    )
}

//Defining the bottom navigation bar

@Composable
fun BottomNavigation(
    currentSection: BottomNavigationSection = BottomNavigationSection.Home,
    onSectionSelected: (BottomNavigationSection) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.align(Alignment.Start).padding(bottom = 14.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        )

        {
            // 1. Home / Today
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSectionSelected(BottomNavigationSection.Home) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TodayIcon(
                    selected = if (currentSection == BottomNavigationSection.Home) TodayIconSelected.On else TodayIconSelected.Off
                )

                Text(
                    text = "Today",
                    fontSize = 12.sp,
                    fontFamily = FustatFamily,
                    color = if (currentSection == BottomNavigationSection.Home) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }

            // 2. Completed
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSectionSelected(BottomNavigationSection.Completed) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompletedIcon(selected = currentSection == BottomNavigationSection.Completed)

                Text(
                    text = "Completed",
                    fontSize = 12.sp,
                    fontFamily = FustatFamily,
                    color = if (currentSection == BottomNavigationSection.Completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }

            // 3. Friends
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSectionSelected(BottomNavigationSection.Friends) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FriendsIcon(selected = currentSection == BottomNavigationSection.Friends)
                Text(
                    text = "Friends",
                    fontSize = 12.sp,
                    fontFamily = FustatFamily,
                    color = if (currentSection == BottomNavigationSection.Friends) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }

            // 4. Rewards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSectionSelected(BottomNavigationSection.Rewards) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RewardsIcon(selected = currentSection == BottomNavigationSection.Rewards)
                Text(
                    text = "Rewards",
                    fontSize = 12.sp,
                    fontFamily = FustatFamily,
                    color = if (currentSection == BottomNavigationSection.Rewards) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}