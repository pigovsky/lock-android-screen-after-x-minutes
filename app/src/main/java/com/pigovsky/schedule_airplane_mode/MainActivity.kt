package com.pigovsky.schedule_airplane_mode

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private var canSpeak = false

    val onSpeechInit = TextToSpeech.OnInitListener { status ->
        if (status == TextToSpeech.SUCCESS) {
            canSpeak = true
            textToSpeech!!.setLanguage(Locale.UK)
            textToSpeech!!.setSpeechRate(1.0f)
            textToSpeech!!.speak("Привіт, Єва!", TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            canSpeak = false
        }
    }
    var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Replace with your layout

        textToSpeech = TextToSpeech(this, onSpeechInit)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        // Example: Request admin privileges
        if (!devicePolicyManager.isAdminActive(compName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "This app needs device administrator access to lock the screen."
            )
            startActivityForResult(
                intent,
                REQUEST_CODE_ENABLE_ADMIN
            ) // Define REQUEST_CODE_ENABLE_ADMIN
        } else {
            // Admin is already active
            Toast.makeText(this, "Admin is already active", Toast.LENGTH_SHORT).show()
        }

        val textView: TextView = findViewById(R.id.textView)

        // Example: Button to lock the screen
        val lockScreenButton: Button = findViewById(R.id.lock_screen_button)
        val editTextNumberMin = findViewById<EditText>(R.id.editTextNumberMin)
        val editTextNumberSec = findViewById<EditText>(R.id.editTextNumberSec)

        lockScreenButton.setOnClickListener {
            val minutes = try {
                editTextNumberMin.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }
            val seconds = try {
                editTextNumberSec.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }
            val delayInSeconds = minutes * 60 + seconds
            val time = scheduleAlarm(this, delayInSeconds)
            textView.text = getString(R.string.alarm_scheduled_at, time)
            lockScreenButton.isEnabled = false
        }

        // Observe LiveData for messages from the AlarmReceiver
        AppEvents.alarmMessage.observe(this, Observer { message ->
            textToSpeech!!.speak(
                "Єва, інтернет закінчився! Слухай маму і тата!",
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
            lockScreenButton.isEnabled = true
            textView.text = message
            Toast.makeText(this, "From Alarm: $message", Toast.LENGTH_LONG).show()

            // Optional: Handle one-shot event
            // AppEvents.consumeEvent()
        })
    }

    // Handle the result of the admin activation request (optional but good practice)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                // Admin enabled successfully
            } else {
                // Admin enabling failed or was cancelled by user
            }
        }
    }

    override fun onDestroy() {
        // Don't forget to shutdown TTS!
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
            Log.d("TextToSpeech", "TTS Destroyed")
        }
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE_ENABLE_ADMIN = 101 // Example request code
    }
}
