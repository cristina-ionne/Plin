package com.plin.ui

import FustatFamily
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Task
import com.plin.ui.theme.TaskAppearance
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import com.plin.ui.theme.PlinOrange
import com.plin.ui.theme.PlinWhite
import kotlin.math.hypot


private val CheckGreen = Color(0xFF16A34A)

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
    val currentWeek by viewModel.currentWeek.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTaskKey by remember { mutableStateOf<String?>(null) }
    val selectedTask = tasks.find { "${it.id}-${it.instanceNumber}" == selectedTaskKey }
    val currentWeekKey = currentWeek?.key.orEmpty()
    var showAddOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshMainView()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp)
        ) {
            TodayCard()
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(tasks, key = { "${it.id}-${it.instanceNumber}" }) { task ->
                        TaskRow(
                            task = task,
                            showWeek = false,
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

    selectedTask?.let { task ->
        TaskInspectDialog(
            task = task,
            currentWeekKey = currentWeekKey,
            onDismiss = { selectedTaskKey = null },
            onUpdate = { updated -> viewModel.updateTask(updated) },
            onCompletedChange = { completed -> viewModel.setCompleted(task, completed) },
            onDelete = {
                viewModel.deleteTask(task)
                selectedTaskKey = null
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskRow(
    task: Task,
    onCompletedChange: (Boolean) -> Unit, 
    showWeek: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    // 1. Progressive filling animation (from 0f to 1f)
    val fillProgress by animateFloatAsState(
        targetValue = if (task.completed) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "liquid_fill_progress"
    )

    // 2. Text color animation to guarantee maximum readability
    val titleTextColor by animateColorAsState(
        targetValue = if (task.completed) PlinWhite else TaskAppearance.textColorPrimary,
        animationSpec = tween(durationMillis = 300),
        label = "title_color"
    )
    val subTextColor by animateColorAsState(
        targetValue = if (task.completed) PlinWhite.copy(alpha = 0.8f) else TaskAppearance.textColorSecondary,
        animationSpec = tween(durationMillis = 300),
        label = "sub_text_color"
    )

    // Main card container with the circular wave animation and the click/long-click handlers
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TaskAppearance.cornerRadius))
            .background(PlinWhite)
            .border(0.dp, TaskAppearance.borderColor, RoundedCornerShape(TaskAppearance.cornerRadius))
            .drawWithContent {
                // A) Draw the default content (background, border, texts, icons) first
                drawContent()

                // B) If task is completed, draw the circular wave animation on top of the content
                if (fillProgress > 0f) {
                    // Checkbox center coordinates (14dp padding + 11dp half dimension of 22dp = 25dp)
                    val originX = 25.dp.toPx()
                    val originY = size.height / 2f

                    // Calculate the maximum radius needed to cover the whole card
                    val maxRadius = hypot(
                        maxOf(originX, size.width - originX),
                        maxOf(originY, size.height - originY)
                    )
                    val currentRadius = maxRadius * fillProgress

                    drawCircle(
                        color = PlinOrange,
                        radius = currentRadius,
                        center = Offset(originX, originY)
                    )

                    // Redraw the content over the orange wave (text and icons)
                    drawContent()
                }
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3. Checkbox animation: Scale-out for the empty circle and Scale-in for the ic_completed_white icon
            AnimatedContent(
                targetState = task.completed,
                label = "checkbox_animation"
            ) { isCompleted ->
                if (isCompleted) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_completed_white),
                        contentDescription = "Task completed",
                        modifier = Modifier
                            .size(22.dp)
                            .animateEnterExit(
                                enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                                exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(PlinWhite, CircleShape)
                            .border(1.dp, TaskAppearance.borderColor, CircleShape)
                            .clickable { onCompletedChange(!task.completed) }
                            .animateEnterExit(
                                enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                                exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty space for checkbox
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 4. Text, title and category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontFamily = FustatFamily,
                    color = titleTextColor
                )
                Text(
                    text = formatCategory(task.category),
                    fontSize = 12.sp,
                    fontFamily = FustatFamily,
                    color = subTextColor
                )
            }

            // 5. Points
            Text(
                text = "${task.points} pts",
                fontSize = 12.sp,
                fontFamily = FustatFamily,
                color = titleTextColor
            )
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

/**
 * Inspect window: edit task details (including assigned week), toggle completed, or delete.
 */
@Composable
private fun TaskInspectDialog(
    task: Task,
    currentWeekKey: String,
    onDismiss: () -> Unit,
    onUpdate: (Task) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    var title by remember(task.id, task.instanceNumber) { mutableStateOf(task.title) }
    var category by remember(task.id, task.instanceNumber) { mutableStateOf(task.category) }
    var pointsText by remember(task.id, task.instanceNumber) { mutableStateOf(task.points.toString()) }
    var isWeekly by remember(task.id, task.instanceNumber) { mutableStateOf(task.isWeekly) }
    var assignedWeek by remember(task.id, task.instanceNumber) { mutableStateOf(task.assignedWeek) }

    fun saveEditsAndDismiss() {
        val trimmed = title.trim()
        val parsedPoints = pointsText.toIntOrNull()?.coerceAtLeast(0) ?: task.points
        val week = assignedWeek.trim()
        if (trimmed.isNotEmpty()) {
            val updated = task.copy(
                title = trimmed,
                category = category,
                points = parsedPoints,
                isWeekly = isWeekly,
                assignedWeek = week,
            )
            if (
                updated.title != task.title ||
                updated.category != task.category ||
                updated.points != task.points ||
                updated.isWeekly != task.isWeekly ||
                updated.assignedWeek != task.assignedWeek
            ) {
                onUpdate(updated)
            }
        }
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = { saveEditsAndDismiss() },
        title = { Text("Task details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Name") },
                )
                CategoryPickerRow(
                    category = category,
                    onCategoryChange = { category = it },
                )
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            pointsText = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Points") },
                )
                OutlinedTextField(
                    value = assignedWeek,
                    onValueChange = { assignedWeek = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Assigned week") },
                    supportingText = {
                        Text("e.g. 2026-W35")
                    },
                )
                if (currentWeekKey.isNotEmpty()) {
                    TextButton(
                        onClick = { assignedWeek = currentWeekKey },
                    ) {
                        Text("Set to current week ($currentWeekKey)")
                    }
                }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = onCompletedChange,
                    )
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                DetailRow(value = formatCreatedAt(task.createdAt))
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = { saveEditsAndDismiss() }) {
                Text("Close")
            }
        },
    )
}

@Composable
private fun DetailRow(value: String) {
    Column {
        Text(
            text = "Created",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun formatCategory(category: TaskCategory): String =
    category.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }

private fun formatCreatedAt(epochMillis: Long): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(epochMillis))
}


@Composable
fun TodayCard() {
    val today = LocalDate.now()
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayValue = today.dayOfWeek.value // 1 (Mon) to 7 (Sun)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF55D3FF))
            .padding(24.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 36.sp,
            fontFamily = FustatFamily,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFF0FAFF)
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
            fontSize = 24.sp,
            fontFamily = FustatFamily,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFF0FAFF)
        )
    }
}