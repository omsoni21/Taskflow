package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * DailyReminderReceiver is triggered by AlarmManager at the scheduled daily time.
 * It counts pending tasks from Room Database and posts the reminder notification,
 * then schedules the next day's alarm.
 */
class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (!DailyReminderScheduler.isReminderEnabled(context)) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val pendingCount = database.taskDao().getPendingTasksCount()
                NotificationHelper.showDailyReminderNotification(context, pendingCount)

                // Reschedule for the next day
                val (hour, minute) = DailyReminderScheduler.getReminderTime(context)
                DailyReminderScheduler.scheduleReminder(context, hour, minute)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
