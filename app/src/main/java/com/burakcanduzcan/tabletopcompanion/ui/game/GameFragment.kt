package com.burakcanduzcan.tabletopcompanion.ui.game

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
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
import com.burakcanduzcan.tabletopcompanion.databinding.PopupAddItemBinding
import com.burakcanduzcan.tabletopcompanion.databinding.PopupScrabbleEnteredWordListBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import com.burakcanduzcan.tabletopcompanion.model.ScrabblePlayer
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil.getFormattedTimeTextFromMilliseconds
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil.getTimeInMilliseconds
import com.burakcanduzcan.tabletopcompanion.utils.ViewUtil
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
        Timber.d(
            "Game: ${selectedGame.name}, player count: ${args.playerCount}, duration per player: ${args.playerRoundDuration}min/${
                getTimeInMilliseconds(args.playerRoundDuration)
            }ms"
        )

        gameLayoutList.add(binding.clScrabble)
        gameLayoutList.add(binding.clChess)
        //....
        setViewsForGame(getString(selectedGame.nameRes))

        return binding.root
    }

    private fun setViewsForGame(gameName: String) {
        Timber.d("Initializing view for $gameName")
        //setting every view to invisible
        for (cl in gameLayoutList) {
            cl.visibility = View.GONE
        }

        when (gameName) {
            //SETUP FOR SCRABBLE GAME
            getString(R.string.scrabble) -> {
                //set game's view visible
                binding.clScrabble.visibility = View.VISIBLE

                //creating players in playerList
                viewModel.createScrabbleGame(args.playerCount)

                //setting up recyclerView with playerList
                binding.rvScrabble.layoutManager = GridLayoutManager(requireContext(), 2)
                val scrabbleAdapter = ScrabbleAdapter(
                    requireContext(),
                    layoutInflater,
                    ::scrabbleOnClickShowEnteredWordList
                )
                binding.rvScrabble.adapter = scrabbleAdapter
                scrabbleAdapter.submitList(viewModel.getAllScrabblePlayers().toMutableList())

                //timer
                var didTimerStarted = false
                val timer: Timer = Timer().apply {
                    setDuration(getTimeInMilliseconds(args.playerRoundDuration))
                    setIsDaemon(false)
                    setStartDelay(0L)
                    setOnTimerListener(object : OnTimerListenerAdapter() {
                        override fun onTimerStarted() {
                            super.onTimerStarted()
                            didTimerStarted = true
                            //change button into stop button
                            binding.ibScrabbleTimerAction.setImageResource(R.drawable.ic_baseline_stop_24)
                            binding.ibScrabbleTimerAction.backgroundTintList =
                                requireContext().getColorStateList(R.color.red)
                        }

                        override fun onTimerRun(milliseconds: Long) {
                            super.onTimerRun(milliseconds)
                            //update timer text
                            binding.tvScrabbleTimer.text =
                                getFormattedTimeTextFromMilliseconds(milliseconds)
                        }

                        override fun onTimerStopped() {
                            super.onTimerStopped()
                            //either PASS or ADD WORD, block out rest
                            //timer
                            didTimerStarted = false
                            binding.tvScrabbleTimer.setTextColor(requireContext().getColor(R.color.gray))
                            //timer button
                            binding.ibScrabbleTimerAction.isEnabled = false
                            binding.ibScrabbleTimerAction.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                            binding.ibScrabbleTimerAction.backgroundTintList =
                                requireContext().getColorStateList(R.color.gray)
                        }

                        override fun onTimerEnded() {
                            super.onTimerEnded()
                            didTimerStarted = false
                            playSound(R.raw.finished)

                            //only option is to PASS
                            //add button
                            binding.ibScrabbleAddWord.isEnabled = false
                            binding.ibScrabbleAddWord.backgroundTintList =
                                requireContext().getColorStateList(R.color.gray)
                            //pass button
                            binding.btnScrabblePass.isEnabled = true
                            binding.btnScrabblePass.backgroundTintList =
                                requireContext().getColorStateList(R.color.pass)
                            //timer
                            binding.tvScrabbleTimer.setTextColor(requireContext().getColor(R.color.red))
                            //timer button
                            binding.ibScrabbleTimerAction.isEnabled = false
                            binding.ibScrabbleTimerAction.backgroundTintList =
                                requireContext().getColorStateList(R.color.gray)
                            binding.ibScrabbleTimerAction.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        }
                    }, true)
                }

                //add word button
                binding.ibScrabbleAddWord.setOnClickListener {
                    val dialogBinding = PopupAddItemBinding.inflate(layoutInflater)
                    AlertDialog.Builder(requireContext())
                        .setView(dialogBinding.root)
                        .setTitle(requireContext().getString(R.string.add_word))
                        .setPositiveButton(requireContext().getString(R.string.add)) { _, _ ->
                            if (dialogBinding.etWord.text.toString()
                                    .isNotBlank() && dialogBinding.etPoint.text.toString()
                                    .isNotBlank()
                            ) {
                                //stop timer
                                timer.stop()

                                val enteredWord: String = dialogBinding.etWord.text.toString()
                                val enteredPoint: Int = dialogBinding.etPoint.text.toString()
                                    .toInt()
                                //add these to the list
                                viewModel.getAllScrabblePlayers()[viewModel.scrabbleCurrentPlayer].listOfEnteredWords.add(
                                    enteredWord
                                )
                                viewModel.getAllScrabblePlayers()[viewModel.scrabbleCurrentPlayer].listOfEarnedPoints.add(
                                    enteredPoint
                                )
                                //calculate new sum
                                viewModel.getAllScrabblePlayers()[viewModel.scrabbleCurrentPlayer].totalPoints =
                                    viewModel.getAllScrabblePlayers()[viewModel.scrabbleCurrentPlayer].totalPoints + enteredPoint
                                //update textView
                                binding.rvScrabble.adapter!!.notifyItemChanged(viewModel.scrabbleCurrentPlayer)
                                //progress the game
                                scrabbleProgressTheGame()
                            } else {
                                //display warning that fields were empty, so couldn't added
                                Toast.makeText(
                                    requireContext(),
                                    requireContext().getString(R.string.could_not_add_word),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton(requireContext().getString(R.string.cancel), null)
                        .setCancelable(false)
                        .show()
                }

                //pass button
                binding.btnScrabblePass.setOnClickListener {
                    Timber.d("Player ${viewModel.scrabbleCurrentPlayer + 1} passed their turn")
                    //progress the game
                    scrabbleProgressTheGame()
                }

                //timer button
                binding.ibScrabbleTimerAction.setOnClickListener {
                    if (didTimerStarted) {
                        didTimerStarted = false
                        timer.stop()
                        scrabbleEnableUi()
                    } else {
                        timer.start()
                    }
                }

                //start the game
                scrabbleProgressTheGame()
            }
            //SETUP FOR CHESS GAME
            getString(R.string.chess) -> {
                //set game's view visible
                binding.clChess.visibility = View.VISIBLE

                //update app bar
                (requireActivity() as AppCompatActivity).supportActionBar?.title =
                    "${requireContext().getString(selectedGame.nameRes)} | Game Phase"

                //TIMERS
                val timerPlayer1: Timer = Timer().apply {
                    setDuration(getTimeInMilliseconds(args.playerRoundDuration))
                    binding.tvChessTimePlayer1.text =
                        getFormattedTimeTextFromMilliseconds(
                            getTimeInMilliseconds(args.playerRoundDuration)
                        )
                    setIsDaemon(false)
                    setStartDelay(0L)
                }
                val timerPlayer2: Timer = Timer().apply {
                    setDuration(getTimeInMilliseconds(args.playerRoundDuration))
                    binding.tvChessTimePlayer2.text =
                        getFormattedTimeTextFromMilliseconds(
                            getTimeInMilliseconds(args.playerRoundDuration)
                        )
                    setIsDaemon(false)
                    setStartDelay(0L)
                }

                //timer listeners
                timerPlayer1.setOnTimerListener(object : OnTimerListenerAdapter() {
                    override fun onTimerRun(milliseconds: Long) {
                        super.onTimerRun(milliseconds)
                        binding.tvChessTimePlayer1.text =
                            getFormattedTimeTextFromMilliseconds(milliseconds)
                    }

                    override fun onTimerEnded() {
                        super.onTimerEnded()
                        Timber.d("Chess - Player 1 ran out of time")
                        binding.btnChessPlayer1.setBackgroundColor(requireContext().getColor(R.color.red))
                        chessGameEndedWithTimerRanOut(false)
                    }
                }, true)
                timerPlayer2.setOnTimerListener(object : OnTimerListenerAdapter() {
                    override fun onTimerRun(milliseconds: Long) {
                        super.onTimerRun(milliseconds)
                        binding.tvChessTimePlayer2.text =
                            getFormattedTimeTextFromMilliseconds(milliseconds)
                    }

                    override fun onTimerEnded() {
                        super.onTimerEnded()
                        Timber.d("Chess - Player 2 ran out of time")
                        binding.btnChessPlayer2.setBackgroundColor(requireContext().getColor(R.color.red))
                        chessGameEndedWithTimerRanOut(true)
                    }
                }, true)


                //VIEWS
                binding.ibChessPlayer1.setOnClickListener {
                    ViewUtil.showChangePlayerNameDialog(
                        requireContext(),
                        binding.tvChessPlayer1,
                        layoutInflater
                    )
                }
                binding.ibChessPlayer2.setOnClickListener {
                    ViewUtil.showChangePlayerNameDialog(
                        requireContext(),
                        binding.tvChessPlayer2,
                        layoutInflater
                    )
                }
                binding.btnChessPlayer1.setOnClickListener {
                    timerPlayer1.pause()
                    binding.btnChessPlayer1.isEnabled = false
                    binding.btnChessPlayer1.setBackgroundColor(requireContext().getColor(R.color.gray))
                    timerPlayer2.start()
                    binding.btnChessPlayer2.isEnabled = true
                    binding.btnChessPlayer2.setBackgroundColor(requireContext().getColor(R.color.white))
                    playSound(R.raw.piece_placement)
                }
                binding.btnChessPlayer2.setOnClickListener {
                    timerPlayer1.start()
                    binding.btnChessPlayer1.isEnabled = true
                    binding.btnChessPlayer1.setBackgroundColor(requireContext().getColor(R.color.white))
                    timerPlayer2.pause()
                    binding.btnChessPlayer2.isEnabled = false
                    binding.btnChessPlayer2.setBackgroundColor(requireContext().getColor(R.color.gray))
                    playSound(R.raw.piece_placement)
                }
                binding.ibChessPause.setOnClickListener {
                    timerPlayer1.pause()
                    timerPlayer2.pause()
                    binding.btnChessPlayer1.isEnabled = true
                    binding.btnChessPlayer2.isEnabled = true
                    binding.btnChessPlayer1.setBackgroundColor(requireContext().getColor(R.color.gray))
                    binding.btnChessPlayer2.setBackgroundColor(requireContext().getColor(R.color.gray))

                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (soundMediaPlayer != null) {
            soundMediaPlayer!!.release()
            soundMediaPlayer = null
        }
        viewModel.clearScrabbleGame()
    }

    private fun scrabbleProgressTheGame() {
        viewModel.scrabbleProgressGame()
        //update current player's ui
        binding.rvScrabble.adapter!!.notifyItemChanged(viewModel.scrabbleCurrentPlayer)
        if (viewModel.scrabbleCurrentPlayer == 0) {
            //if previous player is first player, update last player's ui
            binding.rvScrabble.adapter!!.notifyItemChanged(viewModel.getAllScrabblePlayers().size - 1)
        } else {
            //else, update previous player's ui
            binding.rvScrabble.adapter!!.notifyItemChanged(viewModel.scrabbleCurrentPlayer - 1)
        }


        Timber.d("Scrabble: round ${viewModel.scrabbleRound} - player ${viewModel.scrabbleCurrentPlayer + 1}")
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            "${requireContext().getString(selectedGame.nameRes)} | Round ${viewModel.scrabbleRound}, Player ${viewModel.scrabbleCurrentPlayer + 1}"

        //reset ui
        scrabbleResetUi()
    }

    private fun scrabbleResetUi() {
        //only timer action button is enabled, rest is disabled till it is clicked

        //add word button
        binding.ibScrabbleAddWord.isEnabled = false
        binding.ibScrabbleAddWord.backgroundTintList =
            requireContext().getColorStateList(R.color.gray)
        //pass button
        binding.btnScrabblePass.isEnabled = false
        binding.btnScrabblePass.backgroundTintList =
            requireContext().getColorStateList(R.color.gray)
        //timer
        binding.tvScrabbleTimer.text =
            getFormattedTimeTextFromMilliseconds(getTimeInMilliseconds(args.playerRoundDuration))
        binding.tvScrabbleTimer.setTextColor(requireContext().getColor(R.color.black))
        //timer button
        binding.ibScrabbleTimerAction.isEnabled = true
        binding.ibScrabbleTimerAction.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        binding.ibScrabbleTimerAction.backgroundTintList =
            requireContext().getColorStateList(R.color.green)

    }

    private fun scrabbleEnableUi() {
        //bottom ui will be active

        //add word button
        binding.ibScrabbleAddWord.isEnabled = true
        binding.ibScrabbleAddWord.backgroundTintList =
            requireContext().getColorStateList(R.color.blue)
        //pass button
        binding.btnScrabblePass.isEnabled = true
        binding.btnScrabblePass.backgroundTintList =
            requireContext().getColorStateList(R.color.pass)
        //timer
        binding.tvScrabbleTimer.setTextColor(requireContext().getColor(R.color.black))
        //timer button
        binding.ibScrabbleTimerAction.isEnabled = true
        binding.ibScrabbleTimerAction.setImageResource(R.drawable.ic_baseline_stop_24)
        binding.ibScrabbleTimerAction.backgroundTintList =
            requireContext().getColorStateList(R.color.red)
    }

    private fun scrabbleOnClickShowEnteredWordList(scrabblePlayer: ScrabblePlayer) {
        Timber.d("Show entered word list button is clicked for ${scrabblePlayer.playerName}")

        val popupBinding = PopupScrabbleEnteredWordListBinding.inflate(layoutInflater)

        val width = requireActivity().windowManager.defaultDisplay.width * 8 / 10
        val height = requireActivity().windowManager.defaultDisplay.height * 8 / 10
        val popupWindow = PopupWindow(popupBinding.root, width, height).apply {
            animationStyle = androidx.transition.R.style.Animation_AppCompat_Dialog
            showAtLocation(popupBinding.root, Gravity.TOP, 0, 0)
        }

        //initializing view
        val combinedList = ArrayList<String>()
        for (i in 0 until scrabblePlayer.listOfEnteredWords.size) {
            combinedList.add("${scrabblePlayer.listOfEnteredWords[i]} (${scrabblePlayer.listOfEarnedPoints[i]})")
        }

        popupBinding.lvEnteredItems.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            combinedList
        )

        popupBinding.ibBack.setOnClickListener {
            popupWindow.dismiss()
        }
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
        playSound(R.raw.finished)
    }

    private fun playSound(@RawRes soundRes: Int) {
        if (soundMediaPlayer == null) {
            soundMediaPlayer = MediaPlayer.create(requireContext(), soundRes)
            soundMediaPlayer!!.isLooping = false
            soundMediaPlayer!!.start()
        } else {
            //to prevent sounds collapsing;
            //stop, release
            soundMediaPlayer!!.stop()
            soundMediaPlayer!!.release()
            //and recreate
            soundMediaPlayer = MediaPlayer.create(requireContext(), soundRes)
            soundMediaPlayer!!.isLooping = false
            soundMediaPlayer!!.start()
        }
    }
}