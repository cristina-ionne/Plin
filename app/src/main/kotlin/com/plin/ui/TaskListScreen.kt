package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plin.domain.enums.TaskCategory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import com.plin.R
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.layout.ContentScale
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import com.plin.ui.theme.PlinBlue
import com.plin.ui.theme.PlinWhite
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.lazy.rememberLazyListState
import com.plin.ui.theme.PlinColors
import androidx.compose.ui.layout.onSizeChanged

@Composable
fun NavBar( //This is the top navigation bar with the logo and profile icon
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PlinWhite)
            .statusBarsPadding()
            .height(97.dp)
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Logo()
        Profile(onClick = onProfileClick)
    }
}

@Composable
fun Logo() {
    Image(
        painter = painterResource(R.drawable.logo),
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
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = viewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTaskKey by remember { mutableStateOf<String?>(null) }
    val selectedTask = tasks.find { "${it.id}-${it.instanceNumber}" == selectedTaskKey }
    var showAddOverlay by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.refreshMainView()
    }

    LaunchedEffect(tasks.size) {
    if (tasks.isNotEmpty()) {
        listState.animateScrollToItem(0)
    }
}

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp)
        ) {
            TodayCard(
                onSizeMeasured = { height ->
                viewModel.todayCardHeight = height
            }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Tasks list goes here
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(tasks, key = { "${it.id}-${it.instanceNumber}" }) { task ->
                        TaskItem(
                            task = task,
                            onClick = { viewModel.toggleTaskCompletion(task) },
                            onLongClick = { selectedTaskKey = "${task.id}-${task.instanceNumber}" },
                            onCompletedChange = { isDone -> viewModel.setCompleted(task, isDone) }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = { showAddOverlay = true },
                colors = ButtonDefaults.buttonColors(containerColor = PlinBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+ Add Task",
                    fontSize = 16.sp,
                    fontFamily = FustatFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlinWhite 
                )
            }
        }

            //Animations for the overlay and background dimming when adding a task
            //Black background dimming with fade in/out animation
            AnimatedVisibility(
                visible = showAddOverlay,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showAddOverlay = false }
                )
            }

            //Add Task Overlay with scale and fade in/out animation
            AnimatedVisibility(
                visible = showAddOverlay,
                enter = scaleIn(
                    initialScale = 0.8f, 
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
                ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    AddTaskOverlay(
                        onDismiss = { showAddOverlay = false },
                        onSave = { title, category, isWeekly, duration ->
                            viewModel.addTask(title, category, isWeekly, duration)
                            showAddOverlay = false
                        }
                    )
                }
            }
        }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, category, isWeekly ->
                viewModel.addTask(title, category, isWeekly, duration = 1) // Default duration to 1 hour
                showAddDialog = false
            },
        )
    }

    //Animations for the overlay and background dimming when editing a task
    //Black background dimming with fade in/out animation
    AnimatedVisibility(
        visible = selectedTask != null,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { selectedTaskKey = null }
        )
    }

    //Edit Task Overlay with scale and fade in/out animation
    AnimatedVisibility(
        visible = selectedTask != null,
        enter = scaleIn(
            initialScale = 0.8f, 
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
        ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
        exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            selectedTask?.let { task ->
                EditTaskOverlay(
                    task = task,
                    onDismiss = { selectedTaskKey = null },
                    onSave = { updated -> 
                        viewModel.updateTask(updated)
                        selectedTaskKey = null
                    },
                    onDelete = {
                        viewModel.deleteTask(task)
                        selectedTaskKey = null
                    }
                )
            }
        }
    }
 
}



//Form for creating a task. Only user-editable fields — no ids or timestamps.

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: TaskCategory, isWeekly: Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.GENERIC) }
    var isWeekly by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Title") },
                )
                CategoryPickerRow(
                    category = category,
                    onCategoryChange = { category = it },
                    excludedCategories = setOf(TaskCategory.FRIEND_OF_THE_WEEK),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isWeekly,
                        onCheckedChange = { isWeekly = it },
                    )
                    Text(
                        text = "Repeat every week",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = title.trim()
                    if (trimmed.isNotEmpty()) {
                        onSave(trimmed, category, isWeekly)
                    }
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}



@Composable
private fun CategoryPickerRow(
    category: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
    excludedCategories: Set<TaskCategory> = emptySet(),
) {
    var menuOpen by remember { mutableStateOf(false) }
    val options = TaskCategory.entries.filter { it !in excludedCategories }

    Column {
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Box {
            Text(
                text = formatCategory(category),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { menuOpen = true }
                    .padding(vertical = 2.dp),
            )
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(formatCategory(option)) },
                        onClick = {
                            onCategoryChange(option)
                            menuOpen = false
                        },
                    )
                }
            }
        }
        
    }
}


@Composable
fun TodayCard(onSizeMeasured: (Int) -> Unit) {

    val today = LocalDate.now()
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayValue = today.dayOfWeek.value // 1 (Mon) to 7 (Sun)

    Box(
        modifier = Modifier
        .fillMaxWidth()
        .onSizeChanged { size -> onSizeMeasured(size.height)}
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(PlinColors.PlinBlue)
                .padding(24.dp)
        ) {
            Text(
                text = dayName,
                fontSize = 36.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PlinColors.PlinWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF0FAFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(dayValue / 7f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDD6900))
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Label "Day X of 7"
            Text(
                text = "Day $dayValue of 7",
                fontSize = 16.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.Normal,
                color = PlinColors.PlinWhite
            )
        }
    }
}