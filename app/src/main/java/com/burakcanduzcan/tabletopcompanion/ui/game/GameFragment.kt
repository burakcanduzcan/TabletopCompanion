package com.burakcanduzcan.tabletopcompanion.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentGameBinding
import com.burakcanduzcan.tabletopcompanion.databinding.PopupUsernameBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import com.dariobrux.kotimer.Timer
import com.dariobrux.kotimer.interfaces.OnTimerListenerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private val args: GameFragmentArgs by navArgs()
    private lateinit var selectedGame: Game

    //arrayList to hold layouts of different games
    private val gameViewList = ArrayList<ConstraintLayout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGameBinding.inflate(inflater)
        selectedGame = args.gameEnum
        Timber.i("Game: ${selectedGame.name}, player count: ${args.playerCount}, duration per player: ${
            getTimeInMilliSecondsFromInput(args.playTimeSpan)
        }")

        gameViewList.add(binding.clChess)
        //....
        setViewsForGame(getString(selectedGame.nameRes))

        return binding.root
    }

    private fun setViewsForGame(gameName: String) {
        Timber.i("Initializing view for $gameName")
        //setting every view to invisible
        for (cl in gameViewList) {
            cl.visibility = View.GONE
        }

        when (gameName) {
            getString(R.string.scrabble) -> {
            }
            getString(R.string.chess) -> {
                //SETUP FOR CHESS GAME
                //set game view visible
                binding.clChess.visibility = View.VISIBLE


                //TIMERS
                val timerPlayer1: Timer = Timer().apply {
                    setDuration(getTimeInMilliSecondsFromInput(args.playTimeSpan))
                    binding.tvChessTimePlayer1.text =
                        getFormattedTimeText(getTimeInMilliSecondsFromInput(args.playTimeSpan))
                    setIsDaemon(false)
                    setStartDelay(0L)
                }
                val timerPlayer2: Timer = Timer().apply {
                    setDuration(getTimeInMilliSecondsFromInput(args.playTimeSpan))
                    binding.tvChessTimePlayer2.text =
                        getFormattedTimeText(getTimeInMilliSecondsFromInput(args.playTimeSpan))
                    setIsDaemon(false)
                    setStartDelay(0L)
                }

                //timer listeners
                timerPlayer1.setOnTimerListener(object : OnTimerListenerAdapter() {
                    override fun onTimerEnded() {
                        super.onTimerEnded()
                        Timber.i("Player 1 ran out of time")
                        timerPlayer1.stop()
                        timerPlayer2.stop()
                        chessGameEndedWithTimerRanOut(false)
                    }

                    override fun onTimerRun(milliseconds: Long) {
                        super.onTimerRun(milliseconds)
                        binding.tvChessTimePlayer1.text = getFormattedTimeText(milliseconds)
                    }
                }, true)
                timerPlayer2.setOnTimerListener(object : OnTimerListenerAdapter() {
                    override fun onTimerEnded() {
                        super.onTimerEnded()
                        Timber.i("Player 2 ran out of time")
                        timerPlayer1.stop()
                        timerPlayer2.stop()
                        chessGameEndedWithTimerRanOut(true)
                    }

                    override fun onTimerRun(milliseconds: Long) {
                        super.onTimerRun(milliseconds)
                        binding.tvChessTimePlayer2.text = getFormattedTimeText(milliseconds)
                    }
                }, true)


                //VIEWS
                binding.ibChessPlayer1.setOnClickListener {
                    showPlayerNameChangeDialog(binding.tvChessPlayer1)
                }
                binding.ibChessPlayer2.setOnClickListener {
                    showPlayerNameChangeDialog(binding.tvChessPlayer2)
                }
                binding.btnChessPlayer1.setOnClickListener {
                    timerPlayer1.pause()
                    timerPlayer2.start()
                }
                binding.btnChessPlayer2.setOnClickListener {
                    timerPlayer1.start()
                    timerPlayer2.pause()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            "$selectedGame | Game Phase"
    }

    private fun showPlayerNameChangeDialog(tv: TextView) {
        val dialogBinding = PopupUsernameBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setTitle("Change Username")
            .setPositiveButton("Change") { _, _ ->
                tv.text = dialogBinding.etPlayerNewName.text.toString()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getTimeInMilliSecondsFromInput(input: String): Long {
        return if (input == "none") {
            0
        } else {
            val tmpString = input.substringBefore(" Minute")
            (tmpString.toInt() * 60 * 1000).toLong()
        }
    }

    private fun getFormattedTimeText(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(
                hours)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun chessGameEndedWithTimerRanOut(didPlayerOneWin: Boolean) {
        val wonPlayer: String = if (didPlayerOneWin) {
            binding.tvChessPlayer1.text.toString()
        } else {
            binding.tvChessPlayer2.text.toString()
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Game Over")
            .setMessage("$wonPlayer has won the game!")
            .setPositiveButton("Finish game") { _, _ ->
                this.findNavController().navigate(GameFragmentDirections.returnBackToMainScreen())
            }
            .show()
    }
}