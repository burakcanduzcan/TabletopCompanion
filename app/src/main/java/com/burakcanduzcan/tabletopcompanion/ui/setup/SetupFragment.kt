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
    }

    override fun initListeners() {}

    override fun initObservables() {}

    override fun onResume() {
        super.onResume()
        setTitle("${requireContext().getString(viewModel.selectedGame.nameRes)} | Setup Phase")
        setupViews()
    }

    private fun setupViews() {
        //Option1 - Player Count
        var playerCount: Int = viewModel.selectedGame.minPlayer

        if (viewModel.selectedGame.minPlayer == viewModel.selectedGame.maxPlayer) {
            //if game has fixed amount of players, disable increase-decrease buttons
            binding.tvOption1PlayerCount.text = playerCount.toString()
            binding.ibOption1Increase.backgroundTintList =
                requireContext().getColorStateList(R.color.gray)
            binding.ibOption1Decrease.backgroundTintList =
                requireContext().getColorStateList(R.color.gray)
        } else {
            binding.tvOption1PlayerCount.text = playerCount.toString()
            //because minimum number of player is already assigned, disable decrease button for now
            binding.ibOption1Decrease.isEnabled = false
            binding.ibOption1Decrease.backgroundTintList =
                requireContext().getColorStateList(R.color.gray)

            binding.ibOption1Increase.setOnClickListener {
                //if player number can be increased
                if (playerCount < viewModel.selectedGame.maxPlayer) {
                    //enable decrease button
                    binding.ibOption1Decrease.isEnabled = true
                    binding.ibOption1Decrease.backgroundTintList =
                        requireContext().getColorStateList(R.color.decrease)

                    playerCount++
                    binding.tvOption1PlayerCount.text = playerCount.toString()
                    //if current player count is the maximum amount, disable increase button
                    if (playerCount == viewModel.selectedGame.maxPlayer) {
                        binding.ibOption1Increase.isEnabled = false
                        binding.ibOption1Increase.backgroundTintList =
                            requireContext().getColorStateList(R.color.gray)
                    }
                }
            }
            binding.ibOption1Decrease.setOnClickListener {
                //if player number can be decreased
                if (playerCount > viewModel.selectedGame.minPlayer) {
                    //enable increase button
                    binding.ibOption1Increase.isEnabled = true
                    binding.ibOption1Increase.backgroundTintList =
                        requireContext().getColorStateList(R.color.increase)

                    playerCount--
                    binding.tvOption1PlayerCount.text = playerCount.toString()
                    //if current player count is the minimum amount, disable decrease button
                    if (playerCount == viewModel.selectedGame.minPlayer) {
                        binding.ibOption1Decrease.isEnabled = false
                        binding.ibOption1Decrease.backgroundTintList =
                            requireContext().getColorStateList(R.color.gray)
                    }
                }
            }
        }

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
}