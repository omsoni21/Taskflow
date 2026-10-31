package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.viewmodel.TaskFilter

/**
 * EmptyStateView shows an encouraging placeholder when there are no tasks
 * in the active filter category or when a search returns no matching results.
 */
@Composable
fun EmptyStateView(
    filter: TaskFilter,
    searchQuery: String = "",
    onClearSearch: () -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSearching = searchQuery.isNotBlank()

    val (title, subtitle) = if (isSearching) {
        "No matching tasks found" to "We couldn't find any tasks matching \"$searchQuery\". Try checking for typos or searching another keyword."
    } else {
        when (filter) {
            TaskFilter.ALL -> "No tasks yet!" to "Your task list is clear. Tap '+' or the button below to add your first goal for today."
            TaskFilter.PENDING -> "No pending tasks! 🎉" to "You have completed all pending tasks for today. Great productivity!"
            TaskFilter.COMPLETED -> "No completed tasks yet" to "Check off items as you finish them to track your daily progress."
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp)
            .testTag("empty_state_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSearching) Icons.Outlined.SearchOff else Icons.Outlined.CheckCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isSearching) {
            OutlinedButton(
                onClick = onClearSearch,
                shape = CircleShape,
                modifier = Modifier.testTag("clear_search_empty_button")
            ) {
                Text(text = "Clear Search")
            }
        } else if (filter == TaskFilter.ALL) {
            Button(
                onClick = onAddTaskClick,
                shape = CircleShape,
                modifier = Modifier.testTag("empty_state_add_button")
            ) {
                Text(text = "Add Your First Task")
            }
        }
    }
}
