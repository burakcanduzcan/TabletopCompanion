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
    SCRABBLE(R.string.scrabble, R.drawable.scrabble_letter, 2, 4, true),
    CHESS(R.string.chess, R.drawable.knight, 2, 2, true),
    MUNCHKIN(R.string.munchkin, R.drawable.dungeon, 3, 6),
    FARKLE(R.string.farkle, R.drawable.dice, 0, 0);

    companion object {
        fun getList(): List<Game> = entries
    }
}