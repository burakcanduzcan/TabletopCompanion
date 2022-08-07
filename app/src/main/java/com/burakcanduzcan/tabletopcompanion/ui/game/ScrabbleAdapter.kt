package com.burakcanduzcan.tabletopcompanion.ui.game

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.ItemScrabblePlayerBinding
import com.burakcanduzcan.tabletopcompanion.databinding.PopupAddItemBinding
import com.burakcanduzcan.tabletopcompanion.databinding.PopupPlayerNameBinding
import com.burakcanduzcan.tabletopcompanion.model.ScrabblePlayer
import com.burakcanduzcan.tabletopcompanion.utils.TimeUtil
import com.dariobrux.kotimer.Timer
import com.dariobrux.kotimer.interfaces.OnTimerListenerAdapter
import com.google.android.material.snackbar.Snackbar

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
        //return super.getItemCount()
        return dataSet.size
    }

    inner class ScrabbleViewHolder(
        private val binding: ItemScrabblePlayerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(scrabblePlayer: ScrabblePlayer) {
            //player name
            binding.tvScrabblePlayerName.text =
                context.getString(R.string.numbered_player, scrabblePlayer.playerNumber)

            //edit player name button
            binding.ibScrabblePlayerNameChange.setOnClickListener {
                val dialogBinding = PopupPlayerNameBinding.inflate(layoutInflater)
                AlertDialog.Builder(context)
                    .setView(dialogBinding.root)
                    .setTitle(context.getString(R.string.change_player_name))
                    .setPositiveButton(context.getString(R.string.change)) { _, _ ->
                        binding.tvScrabblePlayerName.text =
                            dialogBinding.etPlayerNewName.text.toString()
                    }
                    .setNegativeButton(context.getString(R.string.cancel), null)
                    .setCancelable(false)
                    .show()
            }

            //listView
            val adapter = ArrayAdapter(context,
                android.R.layout.simple_list_item_1,
                scrabblePlayer.listOfEnteredWords)
            binding.lvScrabbleWords.adapter = adapter

            //listView buttons
            binding.ibAddWord.setOnClickListener {
                val dialogBinding = PopupAddItemBinding.inflate(layoutInflater)
                AlertDialog.Builder(context)
                    .setView(dialogBinding.root)
                    .setTitle(context.getString(R.string.add_word))
                    .setPositiveButton(context.getString(R.string.add)) { _, _ ->
                        if (dialogBinding.etWord.text.toString()
                                .isNotBlank() && dialogBinding.etPoint.text.toString().isNotBlank()
                        ) {
                            val enteredWord: String = dialogBinding.etWord.text.toString()
                            val enteredPoint: Int = dialogBinding.etPoint.text.toString().toInt()
                            //add these to the list
                            scrabblePlayer.listOfEnteredWords.add(enteredWord)
                            scrabblePlayer.listOfEarnedPoints.add(enteredPoint)
                            //calculate new sum
                            scrabblePlayer.totalPoints = scrabblePlayer.totalPoints + enteredPoint
                            //show in textView
                            binding.tvScore.text = scrabblePlayer.totalPoints.toString()
                            //update listView
                            adapter.notifyDataSetChanged()

                        } else {
                            //display warning that fields were empty, so couldn't added
                            Toast.makeText(context,
                                context.getString(R.string.could_not_add_word),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(context.getString(R.string.cancel), null)
                    .setCancelable(false)
                    .show()
            }

            //default points = 0
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
                return oldItem.playerNumber == newItem.playerNumber
            }
        }
    }
}