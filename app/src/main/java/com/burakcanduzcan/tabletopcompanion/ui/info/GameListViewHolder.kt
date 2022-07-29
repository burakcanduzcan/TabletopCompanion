package com.burakcanduzcan.tabletopcompanion.ui.info

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.ItemGameCardBinding
import com.burakcanduzcan.tabletopcompanion.model.Game

class GameListViewHolder(
    private val context: Context,
    private val binding: ItemGameCardBinding,
    private val itemClick: (Game) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(game: Game) {
        //card on click listener
        binding.cardView.setOnClickListener {
            itemClick(game)
        }

        //game icon
        binding.circleImageView.setImageResource(game.imageRes)

        //game title
        binding.tvTitle.setText(game.nameRes)

        //description
        if (game.minPlayer != game.maxPlayer) {
            //if player count is a range: M-N Players
            binding.tvDescription.text =
                "${game.minPlayer}-${game.maxPlayer} ${context.getString(R.string.Players)}"
        } else if (game.minPlayer == 0) {
            // if player count isn't given: TBA
            //un-clickable
            binding.tvDescription.text = context.getString(R.string.tba)
            binding.cardView.isEnabled = false
        } else {
            if (game.minPlayer == 1) {
                //if it's a single player game: M Player
                binding.tvDescription.text =
                    "${game.minPlayer} ${context.getString(R.string.Player)}"
            } else {
                //if it has fixed number of players but not one: N Players
                binding.tvDescription.text =
                    "${game.minPlayer} ${context.getString(R.string.Players)}"
            }
        }
    }
}