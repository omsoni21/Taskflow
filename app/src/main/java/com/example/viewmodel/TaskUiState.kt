package com.example.viewmodel

import com.example.data.TaskEntity

/**
 * TaskUiState holds the complete state of the UI:
 * - tasks: List of all tasks loaded from Room
 * - filter: Current filter selection (All, Pending, Completed)
 * - sortOrder: Sorting criteria (Creation Date, Due Date, or Priority)
 * - searchQuery: Keyword used to filter tasks in real-time
 * - isAddTaskDialogOpen: Whether the quick add dialog is visible
 * - userMessage: Feedback message
 */
data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val sortOrder: TaskSortOrder = TaskSortOrder.CREATION_DATE,
    val searchQuery: String = "",
    val isAddTaskDialogOpen: Boolean = false,
    val userMessage: String? = null
) {
    val totalCount: Int get() = tasks.size
    val completedCount: Int get() = tasks.count { it.isCompleted }
    val pendingCount: Int get() = tasks.count { !it.isCompleted }

    val progress: Float
        get() = if (totalCount == 0) 0f else (completedCount.toFloat() / totalCount.toFloat())

    val filteredAndSortedTasks: List<TaskEntity>
        get() {
            val statusFiltered = when (filter) {
                TaskFilter.ALL -> tasks
                TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
                TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            }

            val searchFiltered = if (searchQuery.isBlank()) {
                statusFiltered
            } else {
                val query = searchQuery.trim()
                statusFiltered.filter { task ->
                    task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true) ||
                        task.category.contains(query, ignoreCase = true)
                }
            }

            return when (sortOrder) {
                TaskSortOrder.CREATION_DATE -> {
                    searchFiltered.sortedWith(
                        compareBy<TaskEntity> { it.isCompleted }
                            .thenByDescending { it.createdAt }
                    )
                }
                TaskSortOrder.DUE_DATE -> {
                    searchFiltered.sortedWith(
                        compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.dueDate == null }
                            .thenBy { it.dueDate ?: Long.MAX_VALUE }
                            .thenByDescending { it.createdAt }
                    )
                }
                TaskSortOrder.PRIORITY -> {
                    searchFiltered.sortedWith(
                        compareBy<TaskEntity> { it.isCompleted }
                            .thenByDescending { it.priorityEnum.level }
                            .thenByDescending { it.createdAt }
                    )
                }
            }
        }
}
