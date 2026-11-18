package com.plin.ui

import com.plin.ui.theme.FustatFamily
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.slideOutHorizontally
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.plin.domain.models.Task
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = viewModel(),
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val incompleteTasks = tasks.filter { !it.completed }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.refreshMainView()
    }

    LaunchedEffect(incompleteTasks.size) {
        if (incompleteTasks.isNotEmpty()) {
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
            if (incompleteTasks.isEmpty()) {
                Text(
                    text = "No tasks yet.",
                    fontFamily = FustatFamily,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp)
                )
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(incompleteTasks, key = { it.id }) { task ->
                        // Local state to manage the animations
                        // 0: Normal, 1: Filling, 2: Sliding out
                        var animationState by remember { mutableIntStateOf(0) }
                        val scope = rememberCoroutineScope()

                        AnimatedVisibility (
                            visible = animationState < 2,
                            exit = slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(durationMillis = 500)
                            ) + fadeOut()
                        ) {
                            TaskItem(
                                task = task.copy(completed = task.completed || animationState >= 1),
                                onClick = {
                                    if (!task.completed) {
                                        animationState = 1
                                        scope.launch {
                                            delay(500.milliseconds)
                                            animationState = 2 
                                            delay(800.milliseconds)
                                            viewModel.setCompleted(task, true)
                                        }
                                    }
                                },
                                onLongClick = { onEditTask(task) },
                                onCompletedChange = { isDone ->
                                    if (isDone) {
                                        animationState = 1
                                        scope.launch {
                                            delay(500.milliseconds)
                                            animationState = 2 // Sliding starts
                                            delay(900.milliseconds) // Wait for the slide to finish
                                            viewModel.setCompleted(task, true)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = { onAddTask() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
                    
    }
}