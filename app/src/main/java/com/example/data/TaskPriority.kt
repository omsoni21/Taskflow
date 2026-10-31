package com.example.data

import androidx.compose.ui.graphics.Color

/**
 * TaskPriority defines the priority level of a task:
 * - HIGH: Urgent / Critical tasks (Red indicator)
 * - MEDIUM: Standard priority tasks (Amber / Orange indicator)
 * - LOW: Non-urgent tasks (Green indicator)
 */
enum class TaskPriority(
    val label: String,
    val level: Int,
    val color: Color,
    val containerColor: Color,
    val onContainerColor: Color
) {
    HIGH(
        label = "High",
        level = 3,
        color = Color(0xFFDC2626),
        containerColor = Color(0xFFFEE2E2),
        onContainerColor = Color(0xFF991B1B)
    ),
    MEDIUM(
        label = "Medium",
        level = 2,
        color = Color(0xFFD97706),
        containerColor = Color(0xFFFEF3C7),
        onContainerColor = Color(0xFF92400E)
    ),
    LOW(
        label = "Low",
        level = 1,
        color = Color(0xFF059669),
        containerColor = Color(0xFFD1FAE5),
        onContainerColor = Color(0xFF065F46)
    );

    companion object {
        fun fromString(value: String?): TaskPriority {
            if (value.isNullOrBlank()) return MEDIUM
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                it.label.equals(value, ignoreCase = true)
            } ?: MEDIUM
        }
    }
}
