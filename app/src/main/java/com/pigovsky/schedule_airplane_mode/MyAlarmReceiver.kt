package com.pigovsky.schedule_airplane_mode

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.Toast


class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MyAlarmReceiver", "Alarm triggered! Executing function.")

        val message = "Alarm has triggered at ${System.currentTimeMillis()}"
        AppEvents.postAlarmMessage(message) // <-- UPDATE LIVE DATA HERE

        myFutureFunctionFromAlarm(context)
        // If doing long work, start a Foreground Service here
    }

    private fun myFutureFunctionFromAlarm(context: Context) {
        Log.d("MyAlarmReceiver", "myFutureFunctionFromAlarm() was called.")

        // Your function logic
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val compName = ComponentName(context, MyDeviceAdminReceiver::class.java)
        if (devicePolicyManager.isAdminActive(compName)) {
            try {
                devicePolicyManager.lockNow()
            } catch (e: Throwable) {
                Toast.makeText(context, "Failed to lock the screen.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Device Admin not active.", Toast.LENGTH_SHORT).show()
            // Optionally, prompt to enable admin again
        }
    }
}