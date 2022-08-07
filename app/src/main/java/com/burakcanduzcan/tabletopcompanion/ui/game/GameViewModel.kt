package com.burakcanduzcan.tabletopcompanion.ui.game

import android.app.Application
import androidx.lifecycle.ViewModel
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.model.ScrabblePlayer
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel
@Inject constructor(private val application: Application) : ViewModel() {
    //region Scrabble
    private val scrabblePlayerList = ArrayList<ScrabblePlayer>()

    fun createPlayersForScrabble(playerCount: Int, durationForEachPlayerInString: String) {
        for (i in 1..playerCount) {
            scrabblePlayerList.add(ScrabblePlayer(
                application.applicationContext.getString(R.string.numbered_player, i),
                i,
                TimeUtil.getTimeInMillisecondsFromString(durationForEachPlayerInString)
            ))
        }
    }

    fun getAllScrabblePlayers(): ArrayList<ScrabblePlayer> {
        return scrabblePlayerList
    }

    fun clearScrabblePlayerList() {
        scrabblePlayerList.clear()
    }
    //endregion
}