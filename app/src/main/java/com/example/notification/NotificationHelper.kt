package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

/**
 * NotificationHelper manages creating notification channels and
 * displaying daily pending task reminder notifications.
 */
object NotificationHelper {

    const val CHANNEL_ID = "daily_task_reminders"
    const val CHANNEL_NAME = "Daily Task Reminders"
    const val CHANNEL_DESCRIPTION = "Reminders for your pending daily tasks"
    const val NOTIFICATION_ID = 1001

    /**
     * Creates the notification channel on Android 8.0+ (API 26+).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and sends the daily reminder notification.
     */
    fun showDailyReminderNotification(context: Context, pendingCount: Int) {
        createNotificationChannel(context)

        // PendingIntent to launch the app when notification is tapped
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Daily Task Reminder 📋"
        val message = if (pendingCount > 0) {
            "You have $pendingCount pending task${if (pendingCount > 1) "s" else ""} for today. Tap to get started!"
        } else {
            "All caught up! Tap to review today's achievements or plan new goals."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is denied
        }
    }
}
