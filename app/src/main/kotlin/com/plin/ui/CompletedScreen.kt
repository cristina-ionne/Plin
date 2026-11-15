package com.plin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plin.ui.theme.PlinColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import com.plin.ui.theme.FustatFamily
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CompletedScreen(viewModel: TaskListViewModel = viewModel()) {

    // 1. Force archive loading as soon as this screen is opened
    LaunchedEffect(Unit) {
        viewModel.selectTab(TaskListTab.CURRENT_WEEK)
    }

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp)
        ) {
            WellDoneCard(fixedHeight = viewModel.todayCardHeight)
            Spacer(modifier = Modifier.height(24.dp))

            // Completed tasks list goes here
            if (tasks.isEmpty()) {
                    Text(
                        text = "No completed tasks yet.",
                        fontFamily = FustatFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp)
                    )

            } else {

                    val completedTasks = tasks.filter { it.completed }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            top = 20.dp,
                            bottom = 100.dp
                        ),
                    ) {
                        items(completedTasks, key = { it.id }) { task ->
                            var animationState by remember { mutableIntStateOf(0) }
                            val scope = rememberCoroutineScope()

                            AnimatedVisibility(
                                visible = animationState < 2,
                                exit = slideOutHorizontally(
                                    targetOffsetX = { -it }, // negative to slide left
                                    animationSpec = tween(durationMillis = 500)
                                ) + fadeOut()
                            ) {
                                TaskItem(
                                    task = task.copy(completed = animationState == 0),
                                    onClick = {
                                        if (animationState == 0) {
                                            animationState = 1 // 1. Empty the task
                                            scope.launch {
                                                delay(300.milliseconds) // Pause to show it's empty
                                                animationState = 2 // Start sliding left
                                                delay(500.milliseconds) // Wait for the slide to end
                                                viewModel.setCompleted(task, false) // Back in the DB
                                            }
                                        }
                                    },
                                    onLongClick = { /* Manage here if you want to open the detail view as well */ },
                                    onCompletedChange = { isDone ->
                                        if (!isDone && animationState == 0) {
                                            animationState = 1
                                            scope.launch {
                                                delay(300.milliseconds)
                                                animationState = 2
                                                delay(500.milliseconds)
                                                viewModel.setCompleted(task, false)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }



   



