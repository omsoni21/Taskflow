package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BootReceiver restores scheduled daily reminder alarms after the device reboots.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (DailyReminderScheduler.isReminderEnabled(context)) {
                val (hour, minute) = DailyReminderScheduler.getReminderTime(context)
                DailyReminderScheduler.scheduleReminder(context, hour, minute)
            }
        }
    }
}
