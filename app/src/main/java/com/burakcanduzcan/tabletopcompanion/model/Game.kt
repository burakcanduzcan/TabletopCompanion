package com.burakcanduzcan.tabletopcompanion.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.burakcanduzcan.tabletopcompanion.R

enum class Game(
    @StringRes val nameRes: Int,
    @DrawableRes val imageRes: Int,
    val minPlayer: Int,
    val maxPlayer: Int,
) {
    SCRABBLE(R.string.scrabble, R.drawable.scrabble_letter, 2, 4),
    CHESS(R.string.chess, R.drawable.knight, 2, 2),
    TBA(R.string.tba, R.drawable.ic_launcher_background, 0, 0);

    companion object {
        fun getList(): List<Game> = values().toList()
    }
}