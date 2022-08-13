package com.burakcanduzcan.tabletopcompanion.model

data class ScrabblePlayer(
    var playerName: String,
    var playerNumber: Int,
) {
    val listOfEnteredWords = ArrayList<String>()
    val listOfEarnedPoints = ArrayList<Int>()
    var totalPoints: Int = 0
    var myTurn: Boolean = false
}