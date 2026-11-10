package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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



@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = viewModel(),
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
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