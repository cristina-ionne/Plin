package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign



@Composable
fun WelcomeOverlay(
    onDismiss: () -> Unit,
    checked: Boolean,
    onToggleFriendOfTheWeek: (Boolean) -> Unit){

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(initialScale = 0.9f, animationSpec = spring(dampingRatio = 0.8f,
                stiffness = 300f)) + fadeIn(),
            exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(30.dp)
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to Plin!",
                        fontSize = 36.sp,
                        lineHeight = 40.sp,
                        fontFamily = FustatFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Here you can plan your week without feeling the pressure of performing and completing your to-do list.",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = FustatFamily,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Friend of the Week",
                            fontFamily = FustatFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = checked,
                            onCheckedChange = onToggleFriendOfTheWeek,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                uncheckedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.scale(0.8f).padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Duration Dropdown
                    Text(
                        text = "This feature allows you to pick a random friend from your friends list, and add a task to connect with them. Stay in touch with your friends!",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = FustatFamily,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // "Got it" Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onDismiss() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = "Got it!",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 16.sp,
                                fontFamily = FustatFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

            }
        }
    }
}
