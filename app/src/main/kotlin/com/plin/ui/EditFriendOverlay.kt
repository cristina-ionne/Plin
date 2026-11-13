package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plin.ui.theme.PlinColors
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import com.plin.domain.models.Friend


@Composable
fun EditFriendOverlay(
    friend: Friend,
    onDismiss: () -> Unit,
    onSave: (Friend) -> Unit,
    onDelete: () -> Unit
) {

    var name by remember { mutableStateOf(friend.name) }
    var frequency by remember { mutableIntStateOf(friend.frequency) }

    AnimatedVisibility(
        visible = true,
        enter = scaleIn(initialScale = 0.9f, animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)) + fadeIn(),
        exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut(animationSpec = tween(durationMillis = 200))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Text(text = "Edit friend", fontSize = 36.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(20.dp))

            // Campo Name
            Text(text = "Name", fontSize = 14.sp, fontFamily = FustatFamily, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                textStyle = TextStyle(fontSize = 16.sp, fontFamily = FustatFamily, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), focusedBorderColor = MaterialTheme.colorScheme.onSurface),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))


            // Frequency
            Text(
                text = "On a scale from 1 to 3, how often do you want to hear from your friend?",
                fontSize = 14.sp,
                fontFamily = FustatFamily,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                lineHeight = 14.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
            
            //5 circles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..3) {
                    Icon(
                        imageVector = if (i <= frequency) Icons.Filled.Circle else Icons.Outlined.Circle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { frequency = i },
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REMOVE FRIEND button
            Text(
                text = "Remove friend",
                fontSize = 16.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clickable { 
                        onDelete()
                        onDismiss()
                    }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel / Save buttons
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onDismiss) { 
                    Text("Cancel", color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold)
                }
                Button(
                    onClick = { 
                        onSave(friend.copy(name = name, frequency = frequency)) 
                    }, 
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Save", color = MaterialTheme.colorScheme.onSecondary, fontSize = 16.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}