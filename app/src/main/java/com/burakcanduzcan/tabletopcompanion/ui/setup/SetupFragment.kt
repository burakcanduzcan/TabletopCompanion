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

    private var option1PlayerCount = 0

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
        //Options1: Player Count
        if (enableCheck(requireContext().getString(selectedGame.nameRes), 1)) {
            binding.cvOption1.visibility = View.VISIBLE
            option1PlayerCount = selectedGame.minPlayer
            binding.tvOption1PlayerCount.text = option1PlayerCount.toString()

            binding.ibOption1Increase.setOnClickListener {
                if (option1PlayerCount < selectedGame.maxPlayer) {
                    option1PlayerCount++
                    binding.tvOption1PlayerCount.text = option1PlayerCount.toString()
                }
                binding.ibOption1Decrease.setOnClickListener {
                    if (option1PlayerCount > selectedGame.minPlayer) {
                        option1PlayerCount--
                        binding.tvOption1PlayerCount.text = option1PlayerCount.toString()
                    }
                }
            }
        } else {
            binding.cvOption1.visibility = View.GONE
        }

        //Options2: Timer
        if (enableCheck(requireContext().getString(selectedGame.nameRes), 2)) {
            binding.cvOption2.visibility = View.VISIBLE

            binding.cbOption2Timer.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.etOption2Minute.visibility = View.VISIBLE
                    binding.etOption2Second.visibility = View.VISIBLE
                    binding.tvOption2Text2.visibility = View.VISIBLE
                    binding.tvOption2Text3.visibility = View.VISIBLE
                } else {
                    binding.etOption2Minute.visibility = View.INVISIBLE
                    binding.etOption2Second.visibility = View.INVISIBLE
                    binding.tvOption2Text2.visibility = View.INVISIBLE
                    binding.tvOption2Text3.visibility = View.INVISIBLE
                }
            }

        } else {
            binding.cvOption2.visibility = View.GONE
        }

        binding.btnNext.setOnClickListener {
            //this will proceed to next screen

            //check if a timer is given
            if (binding.cbOption2Timer.isChecked) {
                val timeToSend = (binding.etOption2Minute.text.toString()
                    .toInt() * 60) + binding.etOption2Second.text.toString().toInt()
                this.findNavController().navigate(SetupFragmentDirections.finishSetup(
                    selectedGame,
                    option1PlayerCount,
                    timeToSend
                ))
            } else {
                this.findNavController()
                    .navigate(SetupFragmentDirections.finishSetup(
                        selectedGame,
                        option1PlayerCount
                    ))
            }
        }
    }

    private fun enableCheck(gameName: String, option: Int): Boolean {
        when (option) {
            1 -> {
                if (gameName == getString(R.string.scrabble)) {
                    //show option1 for scrabble
                    return true
                } else if (gameName == getString(R.string.chess)) {
                    //show option1 for chess
                    return true
                }
            }
            2 -> {
                if (gameName == getString(R.string.scrabble)) {
                    //show option2 for scrabble
                    return true
                } else if (gameName == getString(R.string.chess)) {
                    //show option2 for chess
                    return true
                }
            }
            else -> {
                return false
            }
        }
        return false
    }
}