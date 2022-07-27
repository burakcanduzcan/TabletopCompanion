package com.burakcanduzcan.tabletopcompanion.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentInfoBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import timber.log.Timber

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInfoBinding.inflate(inflater)

        binding.rvGameList.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvGameList.adapter = GameListAdapter(getGameList(), ::onClick)

        return binding.root
    }

    private fun getGameList(): ArrayList<Game> {
        return arrayListOf(
            Game(getString(R.string.scrabble), getString(R.string.two_four_players), R.drawable.scrabble_letter),
            Game(getString(R.string.chess), getString(R.string.two_players), R.drawable.knight),
            Game(getString(R.string.farkle), getString(R.string.tba), R.drawable.ic_launcher_background),
            Game(getString(R.string.scrabble), getString(R.string.two_four_players), R.drawable.scrabble_letter),
            Game(getString(R.string.chess), getString(R.string.two_players), R.drawable.knight),
            Game(getString(R.string.farkle), getString(R.string.tba), R.drawable.ic_launcher_background),
            Game(getString(R.string.scrabble), getString(R.string.two_four_players), R.drawable.scrabble_letter),
            Game(getString(R.string.chess), getString(R.string.two_players), R.drawable.knight),
            Game(getString(R.string.farkle), getString(R.string.tba), R.drawable.ic_launcher_background),
            Game(getString(R.string.scrabble), getString(R.string.two_four_players), R.drawable.scrabble_letter),
            Game(getString(R.string.chess), getString(R.string.two_players), R.drawable.knight),
            Game(getString(R.string.farkle), getString(R.string.tba), R.drawable.ic_launcher_background),
        )
    }

    private fun onClick(game: Game) {
        Timber.i("Game ${game.name} is clicked")
    }

}