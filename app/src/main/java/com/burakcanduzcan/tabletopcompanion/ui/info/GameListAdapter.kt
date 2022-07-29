package com.burakcanduzcan.tabletopcompanion.ui.info

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.databinding.ItemGameCardBinding
import com.burakcanduzcan.tabletopcompanion.model.Game

class GameListAdapter(
    private val context: Context,
    private val list: List<Game>,
    private val itemClick: (Game) -> Unit,
) : RecyclerView.Adapter<GameListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemGameCardBinding.inflate(from, parent, false)
        return GameListViewHolder(context, binding, itemClick)
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}