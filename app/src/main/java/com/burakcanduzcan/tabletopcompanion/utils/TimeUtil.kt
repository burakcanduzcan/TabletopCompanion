package com.burakcanduzcan.tabletopcompanion.utils

import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtil {
    fun getFormattedTimeTextFromMilliseconds(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(
                hours
            )
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    fun getTimeInMilliseconds(minute: Int): Long {
        return minute * 60 * 1000L
    }
}