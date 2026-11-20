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
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun AddCategoryOverlay(
    onDismiss: () -> Unit,
    onSave: (String, Color) -> Unit
) {
    var catName by remember { mutableStateOf("") }
    var catColor by remember { mutableStateOf(Color.Black) }
    var isErrorName by remember { mutableStateOf(false) }

    // Color picker
    val colors = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
        Color(0xFF64B5F6), Color(0xFF4DB6AC), Color(0xFF81C784),
        Color(0xFFFFD54F)
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onDismiss() }
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                initialScale = 0.9f, animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 300f
                )
            ) + fadeIn(),
            exit = scaleOut(
                targetScale = 0.9f,
                animationSpec = spring()
            ) + fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .clickable(enabled = false) {}
                    .padding(24.dp)
            ) {
                Text(
                    text = "New category",
                    fontSize = 36.sp,
                    fontFamily = FustatFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Category name
                Text(
                    text = "Category name",
                    fontSize = 14.sp,
                    fontFamily = FustatFamily,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = catName, onValueChange = { catName = it },
                    placeholder = {
                        Text(
                            "Enter category name",
                            fontFamily = FustatFamily,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FustatFamily,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (isErrorName) MaterialTheme.colorScheme.onTertiary.copy(
                            alpha = 0.7f
                        ) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedBorderColor = if (isErrorName) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
                if (isErrorName) {
                    Text(
                        text = "Category name is mandatory",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 12.sp,
                        fontFamily = FustatFamily,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Color
                Text(
                    text = "Category color",
                    fontSize = 14.sp,
                    fontFamily = FustatFamily,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    lineHeight = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                //Color picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(50))
                                .background(color)
                                .clickable { catColor = color }
                                .then(
                                    if (catColor == color) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(50))
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontFamily = FustatFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Button(
                        onClick = {
                            isErrorName = catName.isBlank()

                            if (!isErrorName) {
                                onSave(catName, catColor)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            "Save",
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