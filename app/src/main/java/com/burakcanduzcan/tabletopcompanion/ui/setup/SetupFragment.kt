package com.burakcanduzcan.tabletopcompanion.ui.setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.tvOption1PlayerCount.text = playerCount.toString()

        binding.ibOption1Increase.setOnClickListener {
            if (playerCount < selectedGame.maxPlayer) {
                playerCount++
                binding.tvOption1PlayerCount.text = playerCount.toString()
            }
        }
        binding.ibOption1Decrease.setOnClickListener {
            if (playerCount > selectedGame.minPlayer) {
                playerCount--
                binding.tvOption1PlayerCount.text = playerCount.toString()
            }
        }

        //finish setup button
        binding.btnNext.setOnClickListener {
            if (binding.etOption2Duration.text.toString().isBlank()) {
                binding.etOption2Duration.error =
                    requireContext().getString(R.string.duration_field_can_not_be_empty)
            } else {
                this.findNavController().navigate(SetupFragmentDirections.finishSetup(
                    selectedGame,
                    binding.tvOption1PlayerCount.text.toString().toInt(),
                    binding.etOption2Duration.text.toString()
                ))
            }
        }
    }
}