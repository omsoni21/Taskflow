package com.example.repository

import com.example.data.TaskDao
import com.example.data.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * TaskRepository acts as an abstraction layer between the UI/ViewModel
 * and the Room database, adhering to the Single Source of Truth architecture.
 */
class TaskRepository(private val taskDao: TaskDao) {

    /**
     * Flow of all tasks.
     */
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    /**
     * Flow of tasks sorted by due date.
     */
    val tasksSortedByDueDate: Flow<List<TaskEntity>> = taskDao.getTasksSortedByDueDate()

    /**
     * Flow of tasks sorted by creation date.
     */
    val tasksSortedByCreationDate: Flow<List<TaskEntity>> = taskDao.getTasksSortedByCreationDate()

    /**
     * Fetches a specific task by its ID.
     */
    fun getTaskById(taskId: Long): Flow<TaskEntity?> = taskDao.getTaskById(taskId)

    suspend fun getTaskByIdSync(taskId: Long): TaskEntity? = taskDao.getTaskByIdSync(taskId)

    /**
     * Adds a new task or updates an existing one.
     */
    suspend fun saveTask(
        id: Long = 0,
        title: String,
        description: String = "",
        category: String = "General",
        priority: String = "Medium",
        isCompleted: Boolean = false,
        dueDate: Long? = null
    ): Long {
        val existingTask = if (id > 0) taskDao.getTaskByIdSync(id) else null
        val task = TaskEntity(
            id = id,
            title = title.trim(),
            description = description.trim(),
            category = category.trim().ifEmpty { "General" },
            priority = priority,
            isCompleted = existingTask?.isCompleted ?: isCompleted,
            createdAt = existingTask?.createdAt ?: System.currentTimeMillis(),
            dueDate = dueDate
        )

        return if (id == 0L) {
            taskDao.insertTask(task)
        } else {
            taskDao.updateTask(task)
            id
        }
    }

    /**
     * Toggles the completion status of an existing task.
     */
    suspend fun toggleTaskStatus(task: TaskEntity) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        taskDao.updateTask(updatedTask)
    }

    /**
     * Deletes a specific task.
     */
    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    /**
     * Clears all tasks that have been marked as completed.
     */
    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}
