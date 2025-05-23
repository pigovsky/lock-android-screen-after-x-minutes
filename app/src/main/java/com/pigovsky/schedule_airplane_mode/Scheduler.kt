package com.pigovsky.schedule_airplane_mode

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar
import java.util.Date
import android.provider.Settings // Required for ACTION_REQUEST_SCHEDULE_EXACT_ALARM


fun scheduleAlarm(context: Context, delayInSeconds: Int): Date {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MyAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0, // requestCode - unique for each PendingIntent
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // Use FLAG_IMMUTABLE
    )

    // Example: Schedule for 30 seconds from now
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        add(Calendar.SECOND, delayInSeconds)
    }

    // Check for permission if setting exact alarms on Android 12+
    val time = calendar.time
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("Scheduler", "Exact alarm scheduled for $time")
        } else {
            // Permission NOT granted
            Log.w("Scheduler", "Cannot schedule exact alarms. Permission needed.")
            // Option 1: Guide the user to settings to grant the permission
            // You should explain to the user why this permission is needed.
            val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            // Optionally, you can add your app's package to the intent
            // to take the user directly to your app's setting for this permission.
            // settingsIntent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(settingsIntent) // Make sure to start this from an Activity context

            // Option 2: Fall back to an inexact alarm if appropriate for your use case
            // alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 60000, pendingIntent) // Example: 1-minute window
            // Log.d("Scheduler", "Falling back to inexact alarm for $time")

            // Option 3: Inform the user and disable the feature that requires exact alarms
        }
    } else {
        scheduleUnexact(alarmManager, calendar, pendingIntent, time)
    }
    return time
}

private fun scheduleUnexact(
    alarmManager: AlarmManager,
    calendar: Calendar,
    pendingIntent: PendingIntent,
    time: Date?
) {
    // For older versions or when exactness is not critical
    alarmManager.setExact( // Or setExactAndAllowWhileIdle for more precision
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
    Log.d("Scheduler", "Alarm scheduled for $time")
}
