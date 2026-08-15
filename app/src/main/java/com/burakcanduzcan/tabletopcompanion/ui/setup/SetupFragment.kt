package com.burakcanduzcan.tabletopcompanion.ui.setup

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.core.BaseFragment
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentSetupBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SetupFragment : BaseFragment<FragmentSetupBinding>(FragmentSetupBinding::inflate) {

    override val viewModel: SetupViewModel by viewModels()
    private val args: SetupFragmentArgs by navArgs()

    override fun initUi() {
        viewModel.selectedGame = args.gameEnum
        viewModel.setDefaultPlayerCount()

        //region Option1 - Player Count
        if (viewModel.selectedGame.minPlayer == viewModel.selectedGame.maxPlayer) {
            //if game has fixed amount of players, disable increase-decrease buttons
            binding.ibOption1Increase.backgroundTintList =
                requireContext().getColorStateList(R.color.gray)
            binding.ibOption1Decrease.backgroundTintList =
                requireContext().getColorStateList(R.color.gray)
        } else {
            //because minimum number of player is already assigned, disable decrease button for now
            binding.ibOption1Decrease.isEnabled = false
            binding.ibOption1Decrease.backgroundTintList =
                requireContext().getColorStateList(R.color.gray)

            binding.ibOption1Increase.setOnClickListener {
                viewModel.increasePlayerCount()

                //enable decrease button
                binding.ibOption1Decrease.isEnabled = true
                binding.ibOption1Decrease.backgroundTintList =
                    requireContext().getColorStateList(R.color.decrease)

                //if current player count is the maximum amount, disable increase button
                if (viewModel.isPlayerCountMaximum()) {
                    binding.ibOption1Increase.isEnabled = false
                    binding.ibOption1Increase.backgroundTintList =
                        requireContext().getColorStateList(R.color.gray)
                }
            }

            binding.ibOption1Decrease.setOnClickListener {
                viewModel.decreasePlayerCount()

                //enable increase button
                binding.ibOption1Increase.isEnabled = true
                binding.ibOption1Increase.backgroundTintList =
                    requireContext().getColorStateList(R.color.increase)

                //if current player count is the minimum amount, disable decrease button
                if (viewModel.isPlayerCountMinimum()) {
                    binding.ibOption1Decrease.isEnabled = false
                    binding.ibOption1Decrease.backgroundTintList =
                        requireContext().getColorStateList(R.color.gray)
                }
            }
        }
        //endregion

        //region Option2 - Timer
        binding.cvOption2.visibility =
            if (viewModel.selectedGame.enableTimer) View.VISIBLE else View.GONE
        //endregion
    }

    override fun initListeners() {
        binding.btnFinishSetup.setOnClickListener {
            var duration: Int? = null

            if (viewModel.selectedGame.enableTimer) {

                if (binding.etDuration.text.toString().isBlank()) {
                    binding.etDuration.error =
                        requireContext().getString(R.string.duration_field_can_not_be_empty)
                    return@setOnClickListener
                }

                duration = binding.etDuration.text.toString().toInt()
                if (duration !in 1..59) {
                    binding.etDuration.error = "1-59"
                    return@setOnClickListener
                }
            }

            //close keyboard, should it left open
            dismissKeyboard()

            //navigate
            when (viewModel.selectedGame) {
                Game.SCRABBLE -> {
                    this.findNavController().navigate(
                        SetupFragmentDirections.actionSetupFragmentToScrabbleFragment(
                            binding.tvPlayerCount.text.toString().toInt(),
                            duration!!.toInt()
                        )
                    )
                }

                Game.CHESS -> {
                    this.findNavController().navigate(
                        SetupFragmentDirections.actionSetupFragmentToChessFragment(
                            duration!!.toInt()
                        )
                    )
                }

                Game.MUNCHKIN -> {
                    this.findNavController().navigate(
                        SetupFragmentDirections.actionSetupFragmentToMunchkinFragment(
                            binding.tvPlayerCount.text.toString().toInt()
                        )
                    )
                }

                else -> {
                    Timber.d("Unrecognized game")
                }
            }
        }
    }

    override fun initObservables() {
        viewModel.playerCount.observe(viewLifecycleOwner) {
            binding.tvPlayerCount.text = it.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle("${requireContext().getString(viewModel.selectedGame.nameRes)} | Setup Phase")
    }
}