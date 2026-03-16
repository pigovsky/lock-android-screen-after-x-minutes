package com.pigovsky.lock_screen_after

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

        val pendingResult = goAsync()
        myFutureFunctionFromAlarm(context, pendingResult)
        // If doing long work, start a Foreground Service here
    }

    private fun myFutureFunctionFromAlarm(context: Context, pendingResult: BroadcastReceiver.PendingResult) {
        Log.d("MyAlarmReceiver", "myFutureFunctionFromAlarm() was called.")

        // Your function logic
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val compName = ComponentName(context, MyDeviceAdminReceiver::class.java)
        if (devicePolicyManager.isAdminActive(compName)) {
            try {
                Log.d("MyAlarmReceiver", "Admin is active. Starting home intent.")

                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                
                try {
                    context.startActivity(homeIntent)
                } catch (e: Exception) {
                    Log.e("MyAlarmReceiver", "Failed to start home intent", e)
                }

                // Give it a short delay to transition before locking
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try {
                        Log.d("MyAlarmReceiver", "Executing devicePolicyManager.lockNow()")
                        devicePolicyManager.lockNow()
                    } catch (e: Exception) {
                        Log.e("MyAlarmReceiver", "Error during lockNow()", e)
                    } finally {
                        pendingResult.finish()
                    }
                }, 1000)
            } catch (e: Throwable) {
                Log.e("MyAlarmReceiver", "Outer error in MyAlarmReceiver", e)
                pendingResult.finish()
            }
        } else {
            Toast.makeText(context, "Device Admin not active.", Toast.LENGTH_SHORT).show()
            pendingResult.finish()
            // Optionally, prompt to enable admin again
        }
    }
}