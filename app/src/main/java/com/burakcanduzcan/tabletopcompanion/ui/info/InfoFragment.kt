package com.burakcanduzcan.tabletopcompanion.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentInfoBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInfoBinding.inflate(inflater)

        binding.rvGameList.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvGameList.adapter =
            GameListAdapter(requireContext(), Game.getList(), ::onClick)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.app_name)
    }

    private fun onClick(game: Game) {
        this.findNavController().navigate(InfoFragmentDirections.gameSelection(game))
        Timber.i("Game ${game.name} is clicked")
    }

}