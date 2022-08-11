package com.burakcanduzcan.tabletopcompanion.ui.game

import android.app.Application
import androidx.lifecycle.ViewModel
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.model.ScrabblePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel
@Inject constructor(private val application: Application) : ViewModel() {

    private val scrabblePlayerList = ArrayList<ScrabblePlayer>()
    var scrabbleRound: Int = -1
    var scrabbleCurrentPlayer: Int = -1

    fun createScrabbleGame(playerCount: Int) {
        for (i in 1..playerCount) {
            scrabblePlayerList.add(ScrabblePlayer(
                application.applicationContext.getString(R.string.player_numbered,
                    i),
                i,
            ))
        }
    }

    fun getAllScrabblePlayers(): ArrayList<ScrabblePlayer> {
        return scrabblePlayerList
    }

    fun scrabbleProgressGame() {
        if (scrabbleCurrentPlayer < 0 || scrabbleRound < 0) {
            //if game hasn't started, initialize it
            scrabbleCurrentPlayer = 0
            scrabbleRound = 1
        } else if (scrabbleCurrentPlayer < (scrabblePlayerList.size - 1)) {
            //if current player isn't last player, go to next player
            scrabbleCurrentPlayer++
        } else {
            //current player is last player, go back to first one
            scrabbleCurrentPlayer = 0
            scrabbleRound++
        }
    }

    fun clearScrabbleGame() {
        scrabblePlayerList.clear()
        scrabbleRound = -1
        scrabbleCurrentPlayer = -1
    }
}