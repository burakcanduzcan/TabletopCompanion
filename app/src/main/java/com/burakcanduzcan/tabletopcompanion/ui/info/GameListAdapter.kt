package com.burakcanduzcan.tabletopcompanion.ui.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.databinding.ItemGameCardBinding
import com.burakcanduzcan.tabletopcompanion.model.Game

class GameListAdapter(
    private val list: ArrayList<Game>,
    private val itemClick: (Game) -> Unit,
) : RecyclerView.Adapter<GameListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemGameCardBinding.inflate(from, parent, false)
        return GameListViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class GameListViewHolder(
    private val binding: ItemGameCardBinding,
    private val itemClick: (Game) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(game: Game) {
        binding.cardView.setOnClickListener {
            itemClick(game)
        }
        binding.circleImageView.setImageResource(game.image)
        binding.tvTitle.text = game.name
        binding.tvDescription.text = game.description
    }
}