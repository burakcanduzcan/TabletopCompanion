package com.burakcanduzcan.tabletopcompanion.ui.scrabble

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.ItemScrabblePlayerBinding
import com.burakcanduzcan.tabletopcompanion.databinding.PopupPlayerNameBinding
import com.burakcanduzcan.tabletopcompanion.model.ScrabblePlayer

class ScrabbleAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onShowEnteredWordsClick: (ScrabblePlayer) -> Unit,
) : ListAdapter<ScrabblePlayer, ScrabbleAdapter.ScrabbleViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrabbleViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemScrabblePlayerBinding.inflate(from, parent, false)
        return ScrabbleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScrabbleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScrabbleViewHolder(
        private val binding: ItemScrabblePlayerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scrabblePlayer: ScrabblePlayer) {
            with(binding) {
                //player name
                tvPlayerName.text = scrabblePlayer.playerName

                //edit player name button
                ibChangeName.setOnClickListener {
                    //create and show the dialog
                    val dialogBinding = PopupPlayerNameBinding.inflate(layoutInflater)
                    AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setTitle(context.getString(R.string.change_player_name))
                        .setPositiveButton(context.getString(R.string.change)) { _, _ ->
                            if (dialogBinding.etNewName.text.toString().length > 9) {
                                scrabblePlayer.playerName =
                                    dialogBinding.etNewName.text.toString().substring(0, 8)
                            } else {
                                scrabblePlayer.playerName = dialogBinding.etNewName.text.toString()
                            }
                            this@ScrabbleAdapter.notifyItemChanged(adapterPosition)
                        }
                        .setNegativeButton(context.getString(R.string.cancel), null)
                        .setCancelable(false)
                        .show()
                }
            }

            //list of entered words
            val listOfTextViews = arrayListOf(binding.tv1, binding.tv2, binding.tv3, binding.tv4)
            when {
                scrabblePlayer.listOfEnteredWords.isEmpty() -> {
                    //if no word is entered, indicate it with a single textView
                    listOfTextViews[0].apply {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.nothing_so_far)
                    }
                    //and make rest of them gone
                    listOfTextViews.subList(1, 4).forEach {
                        it.visibility = View.GONE
                        it.text = ""
                    }
                }

                scrabblePlayer.listOfEnteredWords.size <= 4 -> {
                    //if there are less than 5 but higher than 0 entered words, show them
                    scrabblePlayer.listOfEnteredWords.forEachIndexed { index, word ->
                        listOfTextViews[index].apply {
                            visibility = View.VISIBLE
                            text = word
                        }
                    }
                    //but if they don't fill whole space, make rest of them gone
                    listOfTextViews.subList(scrabblePlayer.listOfEnteredWords.size, 4).forEach {
                        it.visibility = View.GONE
                        it.text = ""
                    }
                }

                else -> {
                    //the only range that is left is where enteredWords > 4.
                    //in that case, show only two last entered words
                    listOfTextViews.forEach { it.visibility = View.VISIBLE }
                    binding.tv1.text = "..."
                    binding.tv2.text =
                        scrabblePlayer.listOfEnteredWords[scrabblePlayer.listOfEnteredWords.size - 3]
                    binding.tv3.text =
                        scrabblePlayer.listOfEnteredWords[scrabblePlayer.listOfEnteredWords.size - 2]
                    binding.tv4.text = scrabblePlayer.listOfEnteredWords.last()
                }
            }

            //button for showing list of entered words button
            binding.ibEnteredWords.setOnClickListener {
                onShowEnteredWordsClick(getItem(adapterPosition))
            }

            //show player score
            binding.tvScore.text = scrabblePlayer.totalPoints.toString()

            //color indicator
            binding.cvPlayer.strokeColor = if (scrabblePlayer.myTurn) {
                context.getColor(R.color.red)
            } else {
                context.getColor(R.color.white)
            }
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<ScrabblePlayer>() {
        override fun areItemsTheSame(
            oldItem: ScrabblePlayer,
            newItem: ScrabblePlayer,
        ): Boolean {
            return oldItem.playerNumber == newItem.playerNumber
        }

        override fun areContentsTheSame(
            oldItem: ScrabblePlayer,
            newItem: ScrabblePlayer,
        ): Boolean {
            return (oldItem.playerName == newItem.playerName && oldItem.totalPoints == newItem.totalPoints)
        }
    }
}