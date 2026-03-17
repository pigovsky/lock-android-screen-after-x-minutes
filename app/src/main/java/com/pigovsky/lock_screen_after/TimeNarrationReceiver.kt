package com.pigovsky.lock_screen_after

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class TimeNarrationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val startHour = sharedPref.getInt("start_hour", 8)
        val endHour = sharedPref.getInt("end_hour", 9)

        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)

        if (currentHour in startHour until endHour) {
            val germanTime = TimeNarrator.formatTimeInGerman(now)
            Log.d("TimeNarrationReceiver", "Narrating time: $germanTime")
            AppEvents.postAlarmMessage(germanTime) // We reuse the messaging system to speak it
        } else {
            Log.d("TimeNarrationReceiver", "Current hour $currentHour outside of [$startHour, $endHour)")
        }
    }
}