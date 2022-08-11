package com.burakcanduzcan.tabletopcompanion.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.burakcanduzcan.tabletopcompanion.R
import com.burakcanduzcan.tabletopcompanion.databinding.PopupPlayerNameBinding

object ViewUtil {
    fun updatePlayerName(context: Context, textView: TextView, inflater: LayoutInflater) {
        val dialogBinding = PopupPlayerNameBinding.inflate(inflater)
        AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setTitle(context.getString(R.string.change_player_name))
            .setPositiveButton(context.getString(R.string.change)) { _, _ ->
                if (dialogBinding.etNewName.text.toString().length > 9) {
                    textView.text = dialogBinding.etNewName.text.toString().substring(0, 8)
                } else {
                    textView.text = dialogBinding.etNewName.text.toString()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .setCancelable(false)
            .show()
    }
}