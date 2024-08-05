package com.burakcanduzcan.tabletopcompanion.ui.chess

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.core.BaseFragment
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentChessBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil.getFormattedTimeTextFromMilliseconds
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil.timeInMilliseconds
import com.dariobrux.kotimer.interfaces.OnTimerListenerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class ChessFragment : BaseFragment<FragmentChessBinding>(FragmentChessBinding::inflate) {

    override val viewModel: ChessViewModel by viewModels()
    private val args: ChessFragmentArgs by navArgs()

    override fun initUi() {
        val duration = args.roundDurationInMinute.timeInMilliseconds()

        binding.tvTimerOne.text = getFormattedTimeTextFromMilliseconds(duration)
        binding.tvTimerTwo.text = getFormattedTimeTextFromMilliseconds(duration)

        viewModel.setupTimer(viewModel.timerPlayer1, duration)
        viewModel.setupTimer(viewModel.timerPlayer2, duration)
    }

    override fun initListeners() {
        //region timer listeners
        viewModel.timerPlayer1.setOnTimerListener(object : OnTimerListenerAdapter() {
            override fun onTimerRun(milliseconds: Long) {
                super.onTimerRun(milliseconds)
                binding.tvTimerOne.text = getFormattedTimeTextFromMilliseconds(milliseconds)
            }

            override fun onTimerEnded() {
                super.onTimerEnded()
                Timber.d("Chess - Player 1 ran out of time")
                binding.btnPlayerOne.setBackgroundColor(requireContext().getColor(R.color.red))
                gameEndedWithTimerRanOut(false)
            }
        }, true)

        viewModel.timerPlayer2.setOnTimerListener(object : OnTimerListenerAdapter() {
            override fun onTimerRun(milliseconds: Long) {
                super.onTimerRun(milliseconds)
                binding.tvTimerTwo.text =
                    getFormattedTimeTextFromMilliseconds(milliseconds)
            }

            override fun onTimerEnded() {
                super.onTimerEnded()
                Timber.d("Chess - Player 2 ran out of time")
                binding.btnPlayerTwo.setBackgroundColor(requireContext().getColor(R.color.red))
                gameEndedWithTimerRanOut(true)
            }
        }, true)
        //endregion

        binding.btnPlayerOne.setOnClickListener {
            viewModel.timerPlayer1.pause()
            binding.btnPlayerOne.isEnabled = false
            binding.btnPlayerOne.setBackgroundColor(requireContext().getColor(R.color.gray))
            viewModel.timerPlayer2.start()
            binding.btnPlayerTwo.isEnabled = true
            binding.btnPlayerTwo.setBackgroundColor(requireContext().getColor(R.color.white))
            playSound(R.raw.piece_placement)
        }

        binding.btnPlayerTwo.setOnClickListener {
            viewModel.timerPlayer1.start()
            binding.btnPlayerOne.isEnabled = true
            binding.btnPlayerOne.setBackgroundColor(requireContext().getColor(R.color.white))
            viewModel.timerPlayer2.pause()
            binding.btnPlayerTwo.isEnabled = false
            binding.btnPlayerTwo.setBackgroundColor(requireContext().getColor(R.color.gray))
            playSound(R.raw.piece_placement)
        }

        binding.ibPause.setOnClickListener {
            pauseTheGame()
        }
    }

    override fun initObservables() {
    }

    override fun onResume() {
        super.onResume()
        setTitle("${requireContext().getString(Game.CHESS.nameRes)} | Setup Phase")
    }

    override fun onPause() {
        super.onPause()
        pauseTheGame()
    }

    private fun pauseTheGame() {
        viewModel.timerPlayer1.pause()
        viewModel.timerPlayer2.pause()
        binding.btnPlayerOne.isEnabled = true
        binding.btnPlayerTwo.isEnabled = true
        binding.btnPlayerOne.setBackgroundColor(requireContext().getColor(R.color.gray))
        binding.btnPlayerTwo.setBackgroundColor(requireContext().getColor(R.color.gray))
    }

    private fun gameEndedWithTimerRanOut(didPlayerOneWin: Boolean) {
        val wonPlayer: String = if (didPlayerOneWin) {
            binding.tvPlayerOneName.text.toString()
        } else {
            binding.tvPlayerTwoName.text.toString()
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.game_over))
            .setMessage(getString(R.string.has_won_the_game, wonPlayer))
            .setPositiveButton(getString(R.string.finish_game)) { _, _ ->
                this.findNavController()
                    .navigate(ChessFragmentDirections.actionChessFragmentToInfoFragment())
            }
            .setCancelable(false)
            .show()
        playSound(R.raw.finished)
    }
}