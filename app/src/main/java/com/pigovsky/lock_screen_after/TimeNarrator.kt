package com.pigovsky.lock_screen_after

import java.util.Calendar

object TimeNarrator {

    fun formatTimeInGerman(calendar: Calendar): String {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val hourNames = arrayOf(
            "zwölf", "eins", "zwei", "drei", "vier", "fünf", "sechs",
            "sieben", "acht", "neun", "zehn", "elf", "zwölf"
        )

        fun getHourName(h: Int): String = hourNames[h % 12]

        // Round to nearest 5 minutes for narration consistency if needed, 
        // but user examples are exactly on 5 min marks.
        val roundedMinute = (minute / 5) * 5

        return when (roundedMinute) {
            0 -> "es ist ${getHourName(hour)} Uhr"
            5 -> "es ist fünf Minuten nach ${getHourName(hour)}"
            10 -> "es ist zehn Minuten nach ${getHourName(hour)}"
            15 -> "es ist viertel nach ${getHourName(hour)}"
            20 -> "es ist zwanzig Minuten nach ${getHourName(hour)}"
            25 -> "es ist fünfundzwanzig Minuten nach ${getHourName(hour)}"
            30 -> "es ist halb ${getHourName(hour + 1)}"
            35 -> "es ist fünfundzwanzig Minuten vor ${getHourName(hour + 1)}"
            40 -> "es ist zwanzig Minuten vor ${getHourName(hour + 1)}"
            45 -> "es ist viertel vor ${getHourName(hour + 1)}"
            50 -> "es ist zehn Minuten vor ${getHourName(hour + 1)}"
            55 -> "es ist fünf Minuten vor ${getHourName(hour + 1)}"
            else -> "es ist ${getHourName(hour)} Uhr" // Fallback
        }
    }
}