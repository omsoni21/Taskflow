package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * TaskDao provides database access methods for tasks.
 * Includes queries for sorting by Creation Date or Due Date,
 * and filtering by status.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM daily_tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM daily_tasks 
        ORDER BY 
            isCompleted ASC,
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END ASC,
            dueDate ASC,
            createdAt DESC
    """)
    fun getTasksSortedByDueDate(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM daily_tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksSortedByCreationDate(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM daily_tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM daily_tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskByIdSync(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM daily_tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM daily_tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("SELECT COUNT(*) FROM daily_tasks WHERE isCompleted = 0")
    suspend fun getPendingTasksCount(): Int
}
