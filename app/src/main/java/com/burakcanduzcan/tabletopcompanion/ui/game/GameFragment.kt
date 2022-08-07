package com.burakcanduzcan.tabletopcompanion.ui.game

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentGameBinding
import com.burakcanduzcan.tabletopcompanion.databinding.PopupPlayerNameBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil
import com.dariobrux.kotimer.Timer
import com.dariobrux.kotimer.interfaces.OnTimerListenerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()

    private val args: GameFragmentArgs by navArgs()
    private lateinit var selectedGame: Game

    //arrayList to hold layouts of different games
    private val gameLayoutList = ArrayList<ConstraintLayout>()

    private var soundMediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGameBinding.inflate(inflater)

        //getting selected game from navigation component
        selectedGame = args.gameEnum
        Timber.i("Game: ${selectedGame.name}, player count: ${args.playerCount}, duration per player: ${
            TimeUtil.getTimeInMillisecondsFromString(args.playTimeSpan)
        }")

        gameLayoutList.add(binding.clScrabble)
        gameLayoutList.add(binding.clChess)
        //....
        setViewsForGame(getString(selectedGame.nameRes))

        return binding.root
    }

    private fun setViewsForGame(gameName: String) {
        Timber.i("Initializing view for $gameName")
        //setting every view to invisible
        for (cl in gameLayoutList) {
            cl.visibility = View.GONE
        }

        when (gameName) {
            getString(R.string.scrabble) -> {
                //SETUP FOR SCRABBLE GAME
                //set game view visible
                binding.clScrabble.visibility = View.VISIBLE

                //creating players in playerList
                viewModel.createPlayersForScrabble(args.playerCount, args.playTimeSpan)

                //setting up recyclerView
                binding.rvScrabble.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 2)
                    adapter = ScrabbleAdapter(
                        viewModel.getAllScrabblePlayers(),
                        requireContext(),
                        layoutInflater)
                }

                //timer
                var timerStarted = false
                val timer: Timer = Timer().apply {
                    setDuration(TimeUtil.getTimeInMillisecondsFromString(args.playTimeSpan))
                    binding.tvScrabbleTimer.text =
                        TimeUtil.getFormattedTimeTextFromMilliseconds(TimeUtil.getTimeInMillisecondsFromString(
                            args.playTimeSpan))
                    setIsDaemon(false)
                    setStartDelay(0L)
                    setOnTimerListener(object : OnTimerListenerAdapter() {
                        override fun onTimerStarted() {
                            super.onTimerStarted()
                        }

                        override fun onTimerRun(milliseconds: Long) {
                            super.onTimerRun(milliseconds)
                            binding.tvScrabbleTimer.text =
                                TimeUtil.getFormattedTimeTextFromMilliseconds(milliseconds)
                        }

                        override fun onTimerStopped() {
                            super.onTimerStopped()
                            binding.tvScrabbleTimer.text =
                                TimeUtil.getFormattedTimeTextFromMilliseconds(TimeUtil.getTimeInMillisecondsFromString(
                                    args.playTimeSpan))
                        }

                        override fun onTimerEnded() {
                            super.onTimerEnded()
                            playSound(R.raw.cuckoo_clock)

                        }
                    }, true)
                }

                //button
                binding.ibScrabbleTimeAction.setOnClickListener {
                    if (timerStarted) {
                        timerStarted = false
                        timer.stop()
                        //change button's icon and color
                        binding.ibScrabbleTimeAction.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        binding.ibScrabbleTimeAction.backgroundTintList =
                            requireContext().getColorStateList(R.color.green)
                    } else {
                        timerStarted = true
                        timer.start()
                        //change button's icon and color
                        binding.ibScrabbleTimeAction.setImageResource(R.drawable.ic_baseline_stop_24)
                        binding.ibScrabbleTimeAction.backgroundTintList =
                            requireContext().getColorStateList(R.color.red)
                    }
                }
            }
            getString(R.string.chess) -> {
                //SETUP FOR CHESS GAME
                //set game view visible
                binding.clChess.visibility = View.VISIBLE


                //TIMERS
                val timerPlayer1: Timer = Timer().apply {
                    setDuration(TimeUtil.getTimeInMillisecondsFromString(args.playTimeSpan))
                    binding.tvChessTimePlayer1.text =
                        TimeUtil.getFormattedTimeTextFromMilliseconds(TimeUtil.getTimeInMillisecondsFromString(
                            args.playTimeSpan))
                    setIsDaemon(false)
                    setStartDelay(0L)
                }
                val timerPlayer2: Timer = Timer().apply {
                    setDuration(TimeUtil.getTimeInMillisecondsFromString(args.playTimeSpan))
                    binding.tvChessTimePlayer2.text =
                        TimeUtil.getFormattedTimeTextFromMilliseconds(TimeUtil.getTimeInMillisecondsFromString(
                            args.playTimeSpan))
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
                        binding.tvChessTimePlayer1.text =
                            TimeUtil.getFormattedTimeTextFromMilliseconds(milliseconds)
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
                        binding.tvChessTimePlayer2.text =
                            TimeUtil.getFormattedTimeTextFromMilliseconds(milliseconds)
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
                    playSound(R.raw.piece_placement)
                }
                binding.btnChessPlayer2.setOnClickListener {
                    timerPlayer1.start()
                    timerPlayer2.pause()
                    playSound(R.raw.piece_placement)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            "${selectedGame.name} | Game Phase"
    }

    override fun onStop() {
        super.onStop()
        if (soundMediaPlayer != null) {
            soundMediaPlayer!!.release()
            soundMediaPlayer = null
        }
        viewModel.clearScrabblePlayerList()
    }

    private fun showPlayerNameChangeDialog(tv: TextView) {
        val dialogBinding = PopupPlayerNameBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setTitle(getString(R.string.change_player_name))
            .setPositiveButton(getString(R.string.change)) { _, _ ->
                tv.text = dialogBinding.etPlayerNewName.text.toString()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setCancelable(false)
            .show()
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
            .setCancelable(false)
            .show()
    }

    private fun playSound(@RawRes soundRes: Int) {
        if (soundMediaPlayer == null) {
            soundMediaPlayer = MediaPlayer.create(requireContext(), soundRes)
            soundMediaPlayer!!.isLooping = false
            soundMediaPlayer!!.start()
        } else {
            soundMediaPlayer!!.start()
        }
    }
}