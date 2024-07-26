package com.burakcanduzcan.tabletopcompanion.ui.chess

import androidx.lifecycle.ViewModel
import com.dariobrux.kotimer.Timer

class ChessViewModel : ViewModel() {
    lateinit var timerPlayer1: Timer
    lateinit var timerPlayer2: Timer
}