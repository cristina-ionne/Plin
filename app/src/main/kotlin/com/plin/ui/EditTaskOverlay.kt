package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Task
import com.plin.ui.theme.PlinColors
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskOverlay(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: () -> Unit
) {

    var name by remember { mutableStateOf(task.title) }
    var category by remember { mutableStateOf(task.category) }
    var duration by remember { mutableIntStateOf(task.duration) }
    var isWeekly by remember { mutableStateOf(task.isWeekly) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = true,
        enter = scaleIn(initialScale = 0.9f, animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)) + fadeIn(),
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
            Text(text = "Edit task", fontSize = 36.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold, color = PlinColors.PlinDark)
            Spacer(modifier = Modifier.height(20.dp))

            // Campo Name
            Text(text = "Name", fontSize = 14.sp, fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.75f))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                textStyle = TextStyle(fontSize = 16.sp, fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.75f)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = PlinColors.PlinDark.copy(alpha = 0.3f), focusedBorderColor = PlinColors.PlinDark),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Category
            Text(text = "Category", fontSize = 14.sp, fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.75f), modifier = Modifier.padding(bottom = 4.dp))
            //Category menu
            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
            ) {
                // Clickable
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .clickable { isCategoryDropdownExpanded = true }
                ) {
                    Text(
                        text = category.toDisplayString(),
                        color = category.color,
                        fontSize = 16.sp,
                        fontFamily = FustatFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Menu
                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false },
                    containerColor = PlinColors.PlinWhite,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                    .border(1.dp, PlinColors.PlinDark.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .heightIn(max = 205.dp)

                ) {
                    // Menu items
                    TaskCategory.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = cat.toDisplayString(),
                                    color = cat.color, 
                                    fontFamily = FustatFamily,
                                    fontWeight = FontWeight.ExtraBold
                                ) 
                            },
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                            onClick = {
                                category = cat
                                isCategoryDropdownExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = PlinColors.PlinDark.copy(alpha = 0.75f)
                            )
                        )
                        // Add divider between items except for the last one
                        if (cat != TaskCategory.entries.last()) {
                            HorizontalDivider(modifier = Modifier
                            .padding(horizontal = 18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Duration Dropdown
            Text(text = "Duration", fontSize = 14.sp, fontFamily = FustatFamily, color = PlinColors.PlinDark.copy(alpha = 0.75f), modifier = Modifier.padding(bottom = 4.dp))
            
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, PlinColors.PlinDark.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .background(PlinColors.PlinWhite)
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val isSelected = duration > 0

                    Text(
                        text = if (isSelected) {
                            when (duration) {
                                5 -> "5+ hours"
                                6 -> "Less than an hour"
                                1 -> "1 hour"
                                else -> "$duration hours"
                            }
                        } else {
                            "Select duration"
                        },
                        fontSize = 16.sp,
                        fontFamily = FustatFamily,
                        
                        color = if (isSelected) {
                            PlinColors.PlinDark.copy(alpha = 0.75f)
                        } else {
                            PlinColors.PlinDark.copy(alpha = 0.5f)
                        }
                    )

                }

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    containerColor = PlinColors.PlinWhite,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(1.dp, PlinColors.PlinDark.copy(alpha = 0.2f), RoundedCornerShape(12.dp)) .heightIn(max = 205.dp)
                ) {
                    val options = listOf("<1", "1", "2", "3", "4", "5+")
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = if(option == "5+") "5+ hours" else if(option == "<1") "Less than 1 hour" else if(option == "1") "1 hour" else "$option hours", fontFamily = FustatFamily) },
                            onClick = {
                                duration = when (option) {
                                    "5+" -> 5
                                    "<1" -> 6
                                    else -> option.toInt()
                                }
                                isDropdownExpanded = false
                            },

                            colors = MenuDefaults.itemColors(
                                textColor = PlinColors.PlinDark.copy(alpha = 0.75f),
                                disabledTextColor = Color.Gray
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                                
                        )

                        if (option != options.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 1.dp,
                                color = PlinColors.PlinDark.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REMOVE TASK button
            Text(
                text = "Remove task",
                fontSize = 16.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PlinColors.PlinOrange,
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
                    Text("Cancel", color = PlinColors.PlinBlue, fontSize = 16.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold) 
                }
                Button(
                    onClick = { 
                        onSave(task.copy(title = name, category = category, duration = duration, isWeekly = isWeekly)) 
                    }, 
                    colors = ButtonDefaults.buttonColors(containerColor = PlinColors.PlinOrange)
                ) {
                    Text("Save", color = PlinColors.PlinWhite, fontSize = 16.sp, fontFamily = FustatFamily, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}