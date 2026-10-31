package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TaskEntity represents a single task stored in the Room database table "daily_tasks".
 *
 * Each task contains:
 * - id: Unique auto-generated identifier
 * - title: What needs to be done
 * - description: Detailed description and notes for the task
 * - category: Category tag (e.g., "Work", "Personal", "Study", "Health")
 * - priority: Priority level ("High", "Medium", "Low")
 * - isCompleted: Boolean flag indicating if the task is finished
 * - createdAt: Timestamp in milliseconds when the task was created
 * - dueDate: Optional timestamp in milliseconds for when the task is due
 */
@Entity(tableName = "daily_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val priority: String = "Medium",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
) {
    val priorityEnum: TaskPriority
        get() = TaskPriority.fromString(priority)
}
