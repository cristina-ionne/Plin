package com.plin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plin.R

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    isDarkTheme: Boolean
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .height(97.dp)
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo(isDarkTheme)
        Profile(onClick = onProfileClick)
    }
}

@Composable
fun Logo(isDarkTheme: Boolean) {
    Image(
        painter = if (isDarkTheme) painterResource(R.drawable.logo_negative) else painterResource(R.drawable.logo),
        contentDescription = "Logo",
        modifier = Modifier.height(40.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun Profile(onClick: () -> Unit) {
    Image(
        painter = painterResource(R.drawable.ic_profile),
        contentDescription = "Profile",
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    )
}