package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TaskEntity
import com.example.repository.TaskRepository
import com.example.ui.navigation.AppScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * TaskViewModel manages UI-related data and coordinates operations between
 * the UI screens and the TaskRepository.
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    private val _selectedSortOrder = MutableStateFlow(TaskSortOrder.CREATION_DATE)
    private val _searchQuery = MutableStateFlow("")
    private val _userMessage = MutableStateFlow<String?>(null)

    // Current navigation destination
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen

    // Combine tasks Flow from Room with filter, sort, search query, and message states (5 flows)
    val uiState: StateFlow<TaskUiState> = combine(
        repository.allTasks,
        _selectedFilter,
        _selectedSortOrder,
        _searchQuery,
        _userMessage
    ) { tasks, filter, sortOrder, searchQuery, message ->
        TaskUiState(
            tasks = tasks,
            filter = filter,
            sortOrder = sortOrder,
            searchQuery = searchQuery,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskUiState()
    )

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setSortOrder(sortOrder: TaskSortOrder) {
        _selectedSortOrder.value = sortOrder
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun navigateToHome() {
        _currentScreen.value = AppScreen.Home
    }

    fun navigateToAddEdit(taskId: Long? = null) {
        _currentScreen.value = AppScreen.AddEdit(taskId = taskId)
    }

    fun getTaskById(taskId: Long): Flow<TaskEntity?> {
        return repository.getTaskById(taskId)
    }

    /**
     * Saves a new or edited task with full details.
     */
    fun saveTask(
        id: Long = 0,
        title: String,
        description: String = "",
        category: String = "General",
        priority: String = "Medium",
        dueDate: Long? = null,
        onSuccess: () -> Unit = {}
    ) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            _userMessage.value = "Task title cannot be empty"
            return
        }

        viewModelScope.launch {
            repository.saveTask(
                id = id,
                title = trimmedTitle,
                description = description.trim(),
                category = category,
                priority = priority,
                dueDate = dueDate
            )
            _userMessage.value = if (id == 0L) "Task created successfully!" else "Task updated!"
            onSuccess()
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskStatus(task)
            val newStatus = !task.isCompleted
            _userMessage.value = if (newStatus) "Task completed! 🎉" else "Task marked pending"
        }
    }

    fun deleteTask(task: TaskEntity, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _userMessage.value = "Task deleted"
            onDeleted()
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
            _userMessage.value = "Completed tasks cleared"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    /**
     * Factory for creating TaskViewModel instances with a TaskRepository dependency.
     */
    class Factory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
