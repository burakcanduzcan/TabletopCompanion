package com.burakcanduzcan.tabletopcompanion.ui.game_collection

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.burakcanduzcan.tabletopcompanion.BuildConfig
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.core.BaseFragment
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentGameCollectionBinding
import com.burakcanduzcan.tabletopcompanion.model.Game
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameCollectionFragment :
    BaseFragment<FragmentGameCollectionBinding>(FragmentGameCollectionBinding::inflate) {
    override val viewModel: GameCollectionViewModel by viewModels()

    override fun initUi() {
        binding.rvGameList.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvGameList.adapter = GameListAdapter(requireContext(), Game.getList()) { game ->
            safeClick {
                this.findNavController()
                    .navigate(GameCollectionFragmentDirections.gameSelection(game))
                Timber.d("Game ${game.name} is clicked")
            }
        }
    }

    override fun initListeners() {}

    override fun initObservables() {}

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME)
    }
}