package com.example.viewmodel

/**
 * Filter options for viewing tasks on the Home Screen.
 */
enum class TaskFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed")
}
