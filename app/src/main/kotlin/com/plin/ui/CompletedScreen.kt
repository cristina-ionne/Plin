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
import com.plin.ui.theme.TaskAppearance

@Composable
fun CompletedScreen(viewModel: TaskListViewModel = viewModel()) {
    // 1. Force archive loading as soon as this screen is opened
    LaunchedEffect(Unit) {
        viewModel.selectTab(TaskListTab.ARCHIVE)
    }

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    // 2. Completed tasks list
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
    ) {
        items(tasks, key = { "${it.id}-${it.instanceNumber}" }) { task ->
            TaskRow(
                task = task,
                showWeek = true,
                onClick = { viewModel.toggleTaskCompletion(task) },
                onLongClick = { /* Manage here if you want to open the detail view as well */ }
            )
        }
    }
}