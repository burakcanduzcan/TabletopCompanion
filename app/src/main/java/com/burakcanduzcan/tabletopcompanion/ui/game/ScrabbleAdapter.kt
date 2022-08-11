package com.burakcanduzcan.tabletopcompanion.ui.game

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.ItemScrabblePlayerBinding
import com.burakcanduzcan.tabletopcompanion.model.ScrabblePlayer
import com.burakcanduzcan.tabletopcompanion.utils.ViewUtil.updatePlayerName

class ScrabbleAdapter(
    private val dataSet: ArrayList<ScrabblePlayer>,
    private val context: Context,
    private val layoutInflater: LayoutInflater,
) : ListAdapter<ScrabblePlayer, ScrabbleAdapter.ScrabbleViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrabbleViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemScrabblePlayerBinding.inflate(from, parent, false)
        return ScrabbleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScrabbleViewHolder, position: Int) {
        val current = dataSet[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ScrabbleViewHolder(
        private val binding: ItemScrabblePlayerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(scrabblePlayer: ScrabblePlayer) {
            //player name
            binding.tvPlayerName.text =
                context.getString(R.string.player_numbered, scrabblePlayer.playerNumber)

            //edit player name button
            binding.ibChangeName.setOnClickListener {
                updatePlayerName(context, binding.tvPlayerName, layoutInflater)
            }

            //show player score
            binding.tvScore.text = scrabblePlayer.totalPoints.toString()
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<ScrabblePlayer>() {
            override fun areItemsTheSame(
                oldItem: ScrabblePlayer,
                newItem: ScrabblePlayer,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ScrabblePlayer,
                newItem: ScrabblePlayer,
            ): Boolean {
                return oldItem.totalPoints == newItem.totalPoints
            }
        }
    }
}