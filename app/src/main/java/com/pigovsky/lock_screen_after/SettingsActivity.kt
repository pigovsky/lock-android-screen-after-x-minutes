package com.pigovsky.lock_screen_after

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editGreetingText = findViewById<EditText>(R.id.edit_greeting_text)
        val editAlarmText = findViewById<EditText>(R.id.edit_alarm_text)
        val editLanguage = findViewById<EditText>(R.id.edit_language)
        val editStartHour = findViewById<EditText>(R.id.edit_start_hour)
        val editEndHour = findViewById<EditText>(R.id.edit_end_hour)
        val saveButton = findViewById<Button>(R.id.save_settings_button)

        val sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE)

        // Load current settings
        editGreetingText.setText(sharedPref.getString("greeting_text", "Привіт, Єва!"))
        editAlarmText.setText(sharedPref.getString("alarm_text", "Єва, інтернет закінчився! Слухай маму і тата!"))
        editLanguage.setText(sharedPref.getString("language_code", "uk"))
        editStartHour.setText(sharedPref.getInt("start_hour", 8).toString())
        editEndHour.setText(sharedPref.getInt("end_hour", 9).toString())

        saveButton.setOnClickListener {
            with(sharedPref.edit()) {
                putString("greeting_text", editGreetingText.text.toString())
                putString("alarm_text", editAlarmText.text.toString())
                putString("language_code", editLanguage.text.toString())
                putInt("start_hour", editStartHour.text.toString().toIntOrNull() ?: 8)
                putInt("end_hour", editEndHour.text.toString().toIntOrNull() ?: 9)
                apply()
            }
            Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}