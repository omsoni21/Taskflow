package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TaskEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.HeaderSection
import com.example.ui.components.ProgressCard
import com.example.ui.components.TaskItemCard
import androidx.compose.ui.platform.LocalContext
import com.example.notification.DailyReminderScheduler
import com.example.ui.components.ReminderSettingsDialog
import com.example.ui.components.TaskSearchBar
import com.example.viewmodel.TaskViewModel

/**
 * HomeScreen is the central dashboard of My Daily Tasks.
 * Displays greeting, progress, real-time search, sorting & filter controls,
 * and reactive task list.
 */
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showClearConfirmationDialog by remember { mutableStateOf(false) }
    var showReminderSettingsDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }

    // Ensure daily reminder alarm is active on app launch if enabled
    LaunchedEffect(Unit) {
        if (DailyReminderScheduler.isReminderEnabled(context)) {
            val (hour, minute) = DailyReminderScheduler.getReminderTime(context)
            DailyReminderScheduler.scheduleReminder(context, hour, minute)
        }
    }

    // Show transient feedback messages via Snackbar
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.navigateToAddEdit(null) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task"
                    )
                },
                text = { Text("New Task") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_task_fab")
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("tasks_lazy_column"),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Greeting & Date Header
                item(key = "header_section") {
                    HeaderSection(
                        hasCompletedTasks = uiState.completedCount > 0,
                        onClearCompleted = { showClearConfirmationDialog = true },
                        onOpenReminderSettings = { showReminderSettingsDialog = true }
                    )
                }

                // Daily Progress Card
                item(key = "progress_card") {
                    ProgressCard(
                        completedCount = uiState.completedCount,
                        totalCount = uiState.totalCount,
                        progress = uiState.progress
                    )
                }

                // Real-time Search Bar
                item(key = "search_bar") {
                    TaskSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { query -> viewModel.setSearchQuery(query) },
                        onClearQuery = { viewModel.clearSearchQuery() }
                    )
                }

                // Filter & Sort Controls
                item(key = "filter_and_sort_chips") {
                    FilterChipsRow(
                        selectedFilter = uiState.filter,
                        selectedSortOrder = uiState.sortOrder,
                        totalCount = uiState.totalCount,
                        pendingCount = uiState.pendingCount,
                        completedCount = uiState.completedCount,
                        onFilterSelected = { filter -> viewModel.setFilter(filter) },
                        onSortOrderSelected = { sortOrder -> viewModel.setSortOrder(sortOrder) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Task Items or Empty State
                if (uiState.filteredAndSortedTasks.isEmpty()) {
                    item(key = "empty_state") {
                        EmptyStateView(
                            filter = uiState.filter,
                            searchQuery = uiState.searchQuery,
                            onClearSearch = { viewModel.clearSearchQuery() },
                            onAddTaskClick = { viewModel.navigateToAddEdit(null) }
                        )
                    }
                } else {
                    items(
                        items = uiState.filteredAndSortedTasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = { selectedTask -> viewModel.toggleTask(selectedTask) },
                            onDelete = { selectedTask -> taskToDelete = selectedTask },
                            onClick = { selectedTask -> viewModel.navigateToAddEdit(selectedTask.id) }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(task)
                        taskToDelete = null
                    },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { taskToDelete = null },
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Completed Tasks Confirmation Dialog
    if (showClearConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmationDialog = false },
            title = { Text("Clear Completed Tasks?") },
            text = { Text("This will permanently remove all finished tasks.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCompletedTasks()
                        showClearConfirmationDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Daily Reminder Settings Dialog
    if (showReminderSettingsDialog) {
        ReminderSettingsDialog(
            onDismiss = { showReminderSettingsDialog = false }
        )
    }
}
