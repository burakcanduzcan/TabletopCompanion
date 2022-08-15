package com.burakcanduzcan.tabletopcompanion.ui.setup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentSetupBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding
    private val args: SetupFragmentArgs by navArgs()
    private lateinit var selectedGame: Game

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSetupBinding.inflate(inflater)
        selectedGame = args.gameEnum

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            "${requireContext().getString(selectedGame.nameRes)} | Setup Phase"
        setupViews()
    }

    private fun setupViews() {
        //Option1 - Player Count
        var playerCount: Int = selectedGame.minPlayer

        if (selectedGame.minPlayer == selectedGame.maxPlayer) {
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
                if (playerCount < selectedGame.maxPlayer) {
                    //enable decrease button
                    binding.ibOption1Decrease.isEnabled = true
                    binding.ibOption1Decrease.backgroundTintList =
                        requireContext().getColorStateList(R.color.decrease)

                    playerCount++
                    binding.tvOption1PlayerCount.text = playerCount.toString()
                    //if current player count is the maximum amount, disable increase button
                    if (playerCount == selectedGame.maxPlayer) {
                        binding.ibOption1Increase.isEnabled = false
                        binding.ibOption1Increase.backgroundTintList =
                            requireContext().getColorStateList(R.color.gray)
                    }
                }
            }
            binding.ibOption1Decrease.setOnClickListener {
                //if player number can be decreased
                if (playerCount > selectedGame.minPlayer) {
                    //enable increase button
                    binding.ibOption1Increase.isEnabled = true
                    binding.ibOption1Increase.backgroundTintList =
                        requireContext().getColorStateList(R.color.increase)

                    playerCount--
                    binding.tvOption1PlayerCount.text = playerCount.toString()
                    //if current player count is the minimum amount, disable decrease button
                    if (playerCount == selectedGame.minPlayer) {
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
                this.findNavController().navigate(SetupFragmentDirections.finishSetup(
                    selectedGame,
                    binding.tvOption1PlayerCount.text.toString().toInt(),
                    binding.etOption2Duration.text.toString().toInt()
                ))
            } else {
                binding.etOption2Duration.error = "1-59"
            }
        }
    }

    private fun dismissKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus != null) {
            imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.applicationWindowToken, 0)
        }
    }
}