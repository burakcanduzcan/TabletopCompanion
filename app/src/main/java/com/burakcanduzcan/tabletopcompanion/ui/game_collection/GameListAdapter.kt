package com.burakcanduzcan.tabletopcompanion.ui.game_collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.ItemGameCardBinding
import com.burakcanduzcan.tabletopcompanion.model.Game

class GameListAdapter(
    private val gameList: List<Game>,
    private val onGameClick: (Game) -> Unit,
) : RecyclerView.Adapter<GameListAdapter.GameListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemGameCardBinding.inflate(from, parent, false)
        return GameListViewHolder(binding, onGameClick)
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        holder.bind(gameList[position])
    }

    override fun getItemCount(): Int = gameList.size

    inner class GameListViewHolder(
        private val binding: ItemGameCardBinding,
        private val itemClick: (Game) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.cardView.setOnClickListener {
                itemClick(game)
            }

            //game icon
            binding.circleImageView.setImageResource(game.imageRes)

            //game title
            binding.tvTitle.setText(game.nameRes)

            //description
            binding.tvDescription.text = when {
                game.minPlayer == 0 -> {
                    // if player count isn't given: TBA (UN-CLICKABLE)
                    binding.cardView.isEnabled = false
                    itemView.context.getString(R.string.tba)
                }

                game.minPlayer == 1 -> {
                    //if it's a single player game: M Player
                    itemView.context.getString(R.string.numbered_min_player, game.minPlayer)
                }

                game.minPlayer != game.maxPlayer -> {
                    //if player count is a range: M-N Players
                    itemView.context.getString(
                        R.string.numbered_min_max_players,
                        game.minPlayer,
                        game.maxPlayer
                    )
                }

                else -> {
                    //if it has fixed number of players but not one: N Players
                    itemView.context.getString(R.string.numbered_min_players, game.minPlayer)
                }
            }
        }
    }
}