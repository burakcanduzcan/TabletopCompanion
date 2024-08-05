package com.burakcanduzcan.tabletopcompanion.ui.chess

import androidx.lifecycle.ViewModel
import com.dariobrux.kotimer.Timer

class ChessViewModel : ViewModel() {
    val timerPlayer1: Timer = Timer()
    val timerPlayer2: Timer = Timer()

    fun setupTimer(
        timer: Timer,
        duration: Long,
    ) {
        timer.apply {
            setDuration(duration)
            setIsDaemon(false)
            setStartDelay(0L)
        }
    }
}