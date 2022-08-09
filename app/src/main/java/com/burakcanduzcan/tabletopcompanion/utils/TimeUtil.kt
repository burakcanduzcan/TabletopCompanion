package com.burakcanduzcan.tabletopcompanion.utils

import java.util.concurrent.TimeUnit

object TimeUtil {
    fun getFormattedTimeTextFromMilliseconds(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(
                hours)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getTimeInMillisecondsFromInteger(input: Int): Long {
        return input * 60 * 1000L
    }
}