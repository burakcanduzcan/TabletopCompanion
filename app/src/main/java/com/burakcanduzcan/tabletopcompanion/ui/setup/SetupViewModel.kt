package com.burakcanduzcan.tabletopcompanion.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.burakcanduzcan.tabletopcompanion.model.Game

class SetupViewModel : ViewModel() {
    lateinit var selectedGame: Game

    private val _playerCount: MutableLiveData<Int> = MutableLiveData()
    val playerCount: LiveData<Int> = _playerCount

    fun setDefaultPlayerCount() {
        _playerCount.value = selectedGame.minPlayer
    }

    fun increasePlayerCount() {
        _playerCount.value = _playerCount.value!! + 1
    }

    fun decreasePlayerCount() {
        _playerCount.value = _playerCount.value!! - 1
    }

    fun isPlayerCountMaximum(): Boolean = playerCount.value == selectedGame.maxPlayer

    fun isPlayerCountMinimum(): Boolean = playerCount.value == selectedGame.minPlayer
}