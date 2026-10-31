package com.example.viewmodel

/**
 * Sort options for arranging tasks.
 */
enum class TaskSortOrder(val label: String) {
    CREATION_DATE("Creation Date"),
    DUE_DATE("Due Date"),
    PRIORITY("Priority")
}
