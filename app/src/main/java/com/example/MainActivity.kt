package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.repository.TaskRepository
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.AddEditTaskScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyDailyTasksTheme
import com.example.viewmodel.TaskViewModel

/**
 * MainActivity is the single activity entry point of the app.
 * It initializes Room Database, Repository, and ViewModel,
 * and handles top-level screen switching between Home and AddEditTaskScreen.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Database and DAO
        val database = AppDatabase.getDatabase(applicationContext)
        val taskDao = database.taskDao()

        // 2. Initialize the Repository
        val repository = TaskRepository(taskDao)

        // 3. Initialize the ViewModel with custom factory
        val viewModel: TaskViewModel by viewModels {
            TaskViewModel.Factory(repository)
        }

        // 4. Render Jetpack Compose UI with screen routing
        setContent {
            MyDailyTasksTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

                when (val screen = currentScreen) {
                    is AppScreen.Home -> {
                        HomeScreen(viewModel = viewModel)
                    }
                    is AppScreen.AddEdit -> {
                        AddEditTaskScreen(
                            taskId = screen.taskId,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToHome() }
                        )
                    }
                }
            }
        }
    }
}
