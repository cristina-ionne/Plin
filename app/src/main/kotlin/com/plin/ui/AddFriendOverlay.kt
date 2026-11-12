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
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.filled.Circle

@Composable
fun AddFriendOverlay(
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit 
) {
    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableIntStateOf(0) }
    var isError by remember { mutableStateOf(false) }

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
                .background(PlinColors.PlinWhite)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Text(text = "New friend", fontSize = 36.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold, color = PlinColors.PlinDark)
            Spacer(modifier = Modifier.height(20.dp))

            // Name
            Text(text = "Name", fontSize = 14.sp, fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.75f), modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                placeholder = { Text("Enter friend name", fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.5f)) },
                textStyle = TextStyle(fontSize = 16.sp, fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.75f)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (isError) PlinColors.PlinRed.copy(alpha = 0.7f) else PlinColors.PlinDark.copy(alpha = 0.3f),
                    focusedBorderColor = if (isError) PlinColors.PlinRed else PlinColors.PlinDark,
                    cursorColor = PlinColors.PlinDark.copy(alpha = 0.3f)),
                singleLine = true
            )
            if (isError) {
                Text(
                    text = "Friend name is mandatory",
                    color = PlinColors.PlinRed,
                    fontSize = 12.sp,
                    fontFamily = FustatFamily,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Frequency
            Text(
                text = "On a scale from 1 to 3, how often do you want to hear from your friend?",
                fontSize = 14.sp,
                fontFamily = FustatFamily,
                color = PlinColors.PlinDark.copy(alpha = 0.75f),
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
                        tint = PlinColors.PlinOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = PlinColors.PlinBlue, fontSize = 16.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold) }
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            isError = true
                        } else {
                            isError = false
                            onSave(name, frequency)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlinColors.PlinOrange)
                    ) {
                    Text("Save", color = PlinColors.PlinWhite, fontSize = 16.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}