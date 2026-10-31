package com.example.ui.components

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notification.DailyReminderScheduler
import java.util.Locale

/**
 * ReminderSettingsDialog allows users to enable/disable daily reminders,
 * select their preferred reminder time, and test sending a notification immediately.
 */
@Composable
fun ReminderSettingsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(DailyReminderScheduler.isReminderEnabled(context)) }
    val (savedHour, savedMinute) = remember { DailyReminderScheduler.getReminderTime(context) }
    var selectedHour by remember { mutableIntStateOf(savedHour) }
    var selectedMinute by remember { mutableIntStateOf(savedMinute) }
    var testNotificationSent by remember { mutableStateOf(false) }

    // Android 13+ Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            DailyReminderScheduler.triggerInstantTestNotification(context)
            testNotificationSent = true
        }
    }

    val presetTimes = listOf(
        Triple("Morning (9:00 AM)", 9, 0),
        Triple("Noon (12:00 PM)", 12, 0),
        Triple("Evening (6:00 PM)", 18, 0),
        Triple("Night (9:00 PM)", 21, 0)
    )

    fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("reminder_settings_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Daily Reminder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Get notified about your pending daily tasks to stay on track.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Toggle Reminders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Daily Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isEnabled) "Active at ${formatTime(selectedHour, selectedMinute)}" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        modifier = Modifier.testTag("reminder_switch")
                    )
                }

                if (isEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Choose Reminder Time",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset time chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        presetTimes.forEach { (label, hour, minute) ->
                            val isSelected = selectedHour == hour && selectedMinute == minute

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedHour = hour
                                    selectedMinute = minute
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(label, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Instant Test Notification button
                OutlinedButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            DailyReminderScheduler.triggerInstantTestNotification(context)
                            testNotificationSent = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_test_notification_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (testNotificationSent) "Notification Sent! 🔔" else "Send Test Reminder Now"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEnabled) {
                        DailyReminderScheduler.scheduleReminder(context, selectedHour, selectedMinute)
                    } else {
                        DailyReminderScheduler.cancelReminder(context)
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("save_reminder_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_reminder_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
