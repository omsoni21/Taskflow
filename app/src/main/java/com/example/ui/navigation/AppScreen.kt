package com.example.ui.navigation

/**
 * AppScreen defines destination screens in the app:
 * - Home: The main dashboard with task progress, filters, and list.
 * - AddEdit: Screen for creating a new task or editing an existing task's details.
 */
sealed interface AppScreen {
    object Home : AppScreen
    data class AddEdit(val taskId: Long? = null) : AppScreen
}
