package com.burakcanduzcan.tabletopcompanion.ui.setup

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.core.BaseFragment
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentSetupBinding
import dagger.hilt.android.AndroidEntryPoint

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
                //if player number can be decreased
                if (viewModel.playerCount.value!! > viewModel.selectedGame.minPlayer) {
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
        }
        //endregion

        //finish setup button
        binding.btnNext.setOnClickListener {
            val duration = binding.etOption2Duration.text.toString()
            if (duration.isBlank()) {
                binding.etOption2Duration.error =
                    requireContext().getString(R.string.duration_field_can_not_be_empty)
            } else if (duration.toInt() in 1..59) {
                //close keyboard, should it left open

                dismissKeyboard()

                //finish setup
                this.findNavController().navigate(
                    SetupFragmentDirections.finishSetup(
                        viewModel.selectedGame,
                        binding.tvOption1PlayerCount.text.toString().toInt(),
                        binding.etOption2Duration.text.toString().toInt()
                    )
                )
            } else {
                binding.etOption2Duration.error = "1-59"
            }
        }
    }

    override fun initListeners() {}

    override fun initObservables() {
        viewModel.playerCount.observe(viewLifecycleOwner) {
            binding.tvOption1PlayerCount.text = it.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle("${requireContext().getString(viewModel.selectedGame.nameRes)} | Setup Phase")
    }
}