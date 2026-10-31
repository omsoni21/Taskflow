package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * HeaderSection displays the daily greeting, today's formatted date,
 * reminder settings launcher, and a quick action to clear completed tasks.
 */
@Composable
fun HeaderSection(
    hasCompletedTasks: Boolean,
    onClearCompleted: () -> Unit,
    onOpenReminderSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning! ☀️"
            in 12..16 -> "Good afternoon! 🌤️"
            in 17..21 -> "Good evening! 🌆"
            else -> "Good night! 🌙"
        }
    }

    val formattedDate = remember {
        val formatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        formatter.format(Date())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "My Daily Tasks",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Daily Reminder Settings Button
            IconButton(
                onClick = onOpenReminderSettings,
                modifier = Modifier.testTag("reminder_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Daily Reminder Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (hasCompletedTasks) {
                IconButton(
                    onClick = onClearCompleted,
                    modifier = Modifier.testTag("clear_completed_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear completed tasks",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
